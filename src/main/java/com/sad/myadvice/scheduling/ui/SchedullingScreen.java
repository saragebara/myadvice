package com.sad.myadvice.scheduling.ui;

import com.sad.myadvice.advising.service.TranscriptService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.scheduling.service.SchedulingService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SchedullingScreen {

    private final TranscriptService transcriptService;
    private final SchedulingService schedulingService;

    // State shared across methods
    private final ObservableList<CourseSection> availableSections = FXCollections.observableArrayList();
    private final ObservableList<CourseSection> plannedSections   = FXCollections.observableArrayList();
    private final TableView<CourseSection>       availableTable   = new TableView<>();
    private final ListView<CourseSection>        plannedListView  = new ListView<>(plannedSections);
    private final Label                          conflictLabel    = new Label();

    public SchedullingScreen(TranscriptService transcriptService,
                              SchedulingService schedulingService) {
        this.transcriptService = transcriptService;
        this.schedulingService = schedulingService;
    }

    public VBox build(User student) {
        // Reset state on every build so stale data doesn't carry over
        availableSections.clear();
        plannedSections.clear();
        availableTable.getColumns().clear();
        conflictLabel.setText("");

        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // ── Page header ───────────────────────────────────────────────────────
        Label title = pageTitle("Yearly Course Scheduling");
        Label subtitle = bodyLabel(
            "Plan your semester by selecting course sections. Conflicts are detected automatically."
        );
        subtitle.setWrapText(true);

        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);
        goldBar.setMaxWidth(Double.MAX_VALUE);

        // ── Transcript context card ───────────────────────────────────────────
        VBox transcriptCard = buildTranscriptContextCard(student);

        // ── Filters card ──────────────────────────────────────────────────────
        VBox filtersCard = buildFiltersCard();

        // ── Available + Planned cards side by side ────────────────────────────
        HBox bodyRow = new HBox(UITheme.SPACING,
            buildAvailableCard(),
            buildPlannedCard(student)
        );
        HBox.setHgrow(bodyRow, Priority.ALWAYS);
        VBox.setVgrow(bodyRow, Priority.ALWAYS);

        // Load default sections (Fall 2026)
        loadAvailableSections();
        refreshConflicts();

        view.getChildren().addAll(title, subtitle, goldBar, transcriptCard, filtersCard, bodyRow);
        return view;
    }

    // ── Transcript context card ───────────────────────────────────────────────

    private VBox buildTranscriptContextCard(User student) {
        ListView<String> completedList = new ListView<>();
        completedList.setStyle(UITheme.STYLE_LIST_VIEW);
        completedList.setPrefHeight(120);

        ListView<String> inProgressList = new ListView<>();
        inProgressList.setStyle(UITheme.STYLE_LIST_VIEW);
        inProgressList.setPrefHeight(80);

        List<Course> completed  = transcriptService.getCompletedCourses(student);
        List<Course> inProgress = transcriptService.getInProgressCourses(student);

        if (completed.isEmpty()) {
            completedList.getItems().add("No completed courses found.");
        } else {
            completed.forEach(c -> completedList.getItems().add(c.getCode() + " — " + c.getName()));
        }

        if (inProgress.isEmpty()) {
            inProgressList.getItems().add("No in-progress courses found.");
        } else {
            inProgress.forEach(c -> inProgressList.getItems().add(c.getCode() + " — " + c.getName()));
        }

        HBox lists = new HBox(UITheme.SPACING);
        VBox completedBox = new VBox(4, sectionLabel("Completed (" + completed.size() + ")"), completedList);
        VBox inProgressBox = new VBox(4, sectionLabel("In Progress (" + inProgress.size() + ")"), inProgressList);
        HBox.setHgrow(completedBox, Priority.ALWAYS);
        HBox.setHgrow(inProgressBox, Priority.ALWAYS);
        lists.getChildren().addAll(completedBox, inProgressBox);

        VBox card = new VBox(8, goldBar(), sectionLabel("Transcript Context"), lists);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        return card;
    }

    // ── Filters card ──────────────────────────────────────────────────────────

    private VBox buildFiltersCard() {
        Label section = sectionLabel("Search and Filter");

        TextField courseSearch = new TextField();
        courseSearch.setPromptText("Search by course code or title...");
        courseSearch.setStyle(UITheme.STYLE_TEXT_FIELD);
        HBox.setHgrow(courseSearch, Priority.ALWAYS);

        ComboBox<String> termBox = new ComboBox<>();
        termBox.getItems().addAll("Fall", "Winter", "Spring");
        termBox.setValue("Fall");
        termBox.setStyle(UITheme.STYLE_TEXT_FIELD);

        ComboBox<String> yearBox = new ComboBox<>();
        yearBox.getItems().addAll("2026", "2027");
        yearBox.setValue("2026");
        yearBox.setStyle(UITheme.STYLE_TEXT_FIELD);

        Button applyBtn = primaryButton("Apply");
        applyBtn.setOnAction(e ->
            applyFilter(courseSearch.getText(), termBox.getValue(), yearBox.getValue())
        );

        HBox row = new HBox(UITheme.SPACING, courseSearch, termBox, yearBox, applyBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, goldBar(), section, row);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        return card;
    }

    // ── Available sections card ───────────────────────────────────────────────

    private VBox buildAvailableCard() {
        Label section = sectionLabel("Available Sections");

        TableColumn<CourseSection, String> codeCol       = col("Course",     "courseCode");
        TableColumn<CourseSection, String> titleCol      = col("Title",      "courseTitle");
        TableColumn<CourseSection, String> instructorCol = col("Instructor", "instructor");
        TableColumn<CourseSection, String> dayCol        = col("Day",        "day");
        TableColumn<CourseSection, String> timeCol       = col("Time",       "time");
        TableColumn<CourseSection, String> roomCol       = col("Room",       "room");

        availableTable.getColumns().setAll(
            codeCol, titleCol, instructorCol, dayCol, timeCol, roomCol
        );
        availableTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableTable.setStyle(UITheme.STYLE_LIST_VIEW);
        availableTable.setItems(availableSections);
        availableTable.setPrefHeight(280);
        VBox.setVgrow(availableTable, Priority.ALWAYS);

        Button addBtn = primaryButton("➕ Add Selected to Plan");
        addBtn.setOnAction(e -> addSelectedToPlan());

        VBox card = new VBox(8, goldBar(), section, availableTable, addBtn);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(680);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ── Planned timetable card ────────────────────────────────────────────────

    private VBox buildPlannedCard(User student) {
        Label section = sectionLabel("Planned Timetable");

        plannedListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CourseSection item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toDisplayText());
            }
        });
        plannedListView.setStyle(UITheme.STYLE_LIST_VIEW);
        plannedListView.setPrefHeight(280);
        VBox.setVgrow(plannedListView, Priority.ALWAYS);

        conflictLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        conflictLabel.setWrapText(true);

        Button removeBtn = secondaryButton("➖ Remove Selected");
        removeBtn.setOnAction(e -> removeSelectedFromPlan());

        Button saveBtn = primaryButton("💾 Save Schedule");
        saveBtn.setOnAction(e -> saveSchedule(student));

        HBox actions = new HBox(UITheme.SPACING, removeBtn, saveBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8,
            goldBar(), section, plannedListView,
            actions, conflictLabel
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(420);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void applyFilter(String text, String term, String year) {
        String query = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        List<CourseSection> filtered = getAllSeedSections().stream()
            .filter(s -> s.getTerm().equalsIgnoreCase(term))
            .filter(s -> s.getYear().equals(year))
            .filter(s -> query.isBlank()
                || s.getCourseCode().toLowerCase(Locale.ROOT).contains(query)
                || s.getCourseTitle().toLowerCase(Locale.ROOT).contains(query))
            .sorted(Comparator.comparing(CourseSection::getCourseCode)
                .thenComparing(CourseSection::getDay))
            .collect(Collectors.toList());
        availableSections.setAll(filtered);
    }

    private void addSelectedToPlan() {
        List<CourseSection> selected = new ArrayList<>(
            availableTable.getSelectionModel().getSelectedItems()
        );
        for (CourseSection s : selected) {
            if (!plannedSections.contains(s)) plannedSections.add(s);
        }
        refreshConflicts();
    }

    private void removeSelectedFromPlan() {
        CourseSection selected = plannedListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            plannedSections.remove(selected);
            refreshConflicts();
        }
    }

    private void saveSchedule(User student) {
        if (plannedSections.isEmpty()) {
            conflictLabel.setText("⚠ Add at least one section before saving.");
            conflictLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            return;
        }
        if (hasTimeConflict(new ArrayList<>(plannedSections))) {
            conflictLabel.setText("⚠ Cannot save — resolve time conflicts first.");
            conflictLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            return;
        }

        // Convert CourseSection list to string format for SchedulingService
        List<String> sectionStrings = plannedSections.stream()
            .map(s -> s.getCourseCode() + " " + "A" + " | "
                + s.getDay() + " " + s.getTime() + " | "
                + s.getInstructor() + " | "
                + s.getRoom())
            .collect(Collectors.toList());

        schedulingService.saveSchedule(
            student,
            "My Schedule",
            "2026W",
            sectionStrings
        );

        conflictLabel.setText("✓ Schedule saved! " + plannedSections.size() + " sections.");
        conflictLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
    }

    private void refreshConflicts() {
        if (hasTimeConflict(new ArrayList<>(plannedSections))) {
            conflictLabel.setText("⚠ Conflict detected: two or more sections overlap.");
            conflictLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
        } else if (plannedSections.isEmpty()) {
            conflictLabel.setText("");
        } else {
            conflictLabel.setText("✓ No time conflicts.");
            conflictLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
        }
    }

    private boolean hasTimeConflict(List<CourseSection> sections) {
        for (int i = 0; i < sections.size(); i++) {
            for (int j = i + 1; j < sections.size(); j++) {
                CourseSection a = sections.get(i);
                CourseSection b = sections.get(j);
                if (a.getDay().equalsIgnoreCase(b.getDay())
                        && a.getTime().equalsIgnoreCase(b.getTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadAvailableSections() {
        availableSections.setAll(
            getAllSeedSections().stream()
                .filter(s -> "Fall".equalsIgnoreCase(s.getTerm()) && "2026".equals(s.getYear()))
                .collect(Collectors.toList())
        );
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    private List<CourseSection> getAllSeedSections() {
        List<CourseSection> sections = new ArrayList<>();

        // Fall 2026
        sections.add(new CourseSection("COMP-2800", "Software Development",         "Dr. Maniatis",  "Mon", "09:30-10:50", "Erie Hall 210",  "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3150", "Database Management Systems",   "Dr. Ahmed",     "Tue", "11:30-12:50", "Lambton 101",    "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3220", "OO Software Analysis & Design", "Dr. Campbell",  "Wed", "13:00-14:20", "Essex 220",      "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3300", "Operating System Fundamentals", "Dr. Taylor",    "Thu", "10:00-11:20", "Erie Hall 120",  "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3110", "Intro to Software Engineering", "Dr. Singh",     "Fri", "14:30-15:50", "Erie Hall 310",  "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3540", "Theory of Computation",         "Dr. Morgan",    "Mon", "13:00-14:20", "Lambton 205",    "Fall",   "2026"));
        sections.add(new CourseSection("COMP-3670", "Computer Networks",             "Dr. Nguyen",    "Tue", "09:30-10:50", "Essex 110",      "Fall",   "2026"));
        sections.add(new CourseSection("MATH-3940", "Numerical Analysis for CS",     "Prof. Lee",     "Wed", "10:00-11:20", "Memorial 204",   "Fall",   "2026"));

        // Winter 2027
        sections.add(new CourseSection("COMP-4110", "Software Verification & Test",  "Dr. Ibrahim",   "Mon", "09:30-10:50", "Erie Hall 210",  "Winter", "2027"));
        sections.add(new CourseSection("COMP-4400", "Principles of Prog Languages",  "Dr. Kim",       "Tue", "11:30-12:50", "Lambton 101",    "Winter", "2027"));
        sections.add(new CourseSection("COMP-4540", "Design & Analysis of Algo",     "Dr. Patel",     "Wed", "13:00-14:20", "Essex 220",      "Winter", "2027"));
        sections.add(new CourseSection("COMP-4800", "Selected Topics in Soft. Eng.", "Dr. Brown",     "Thu", "10:00-11:20", "Erie Hall 120",  "Winter", "2027"));
        sections.add(new CourseSection("COMP-3057", "Cyber-Ethics",                  "Prof. Park",    "Fri", "14:30-15:50", "Memorial 101",   "Winter", "2027"));
        sections.add(new CourseSection("COMP-3340", "WWW Info Systems Dev",          "Dr. Young",     "Mon", "13:00-14:20", "Lambton 205",    "Winter", "2027"));
        sections.add(new CourseSection("COMP-4990", "Project Management",            "Dr. Maniatis",  "Tue", "09:30-10:50", "Essex 110",      "Winter", "2027"));

        return sections;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private TableColumn<CourseSection, String> col(String header, String property) {
        TableColumn<CourseSection, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    private Region goldBar() {
        Region bar = new Region();
        bar.setStyle(UITheme.STYLE_GOLD_BAR);
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }

    private Label pageTitle(String text) {
        Label l = new Label(text);
        l.setStyle(UITheme.STYLE_PAGE_TITLE);
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle(UITheme.STYLE_SECTION_LABEL);
        return l;
    }

    private Label bodyLabel(String text) {
        Label l = new Label(text);
        l.setStyle(UITheme.STYLE_BODY_LABEL);
        return l;
    }

    private Button primaryButton(String text) {
        Button b = new Button(text);
        b.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        return b;
    }

    private Button secondaryButton(String text) {
        Button b = new Button(text);
        b.setStyle(UITheme.STYLE_SECONDARY_BUTTON);
        return b;
    }

    // ── CourseSection data class (Yousif's, kept intact) ─────────────────────

    public static class CourseSection {
        private final String courseCode;
        private final String courseTitle;
        private final String instructor;
        private final String day;
        private final String time;
        private final String room;
        private final String term;
        private final String year;

        public CourseSection(String courseCode, String courseTitle, String instructor,
                             String day, String time, String room, String term, String year) {
            this.courseCode  = courseCode;
            this.courseTitle = courseTitle;
            this.instructor  = instructor;
            this.day         = day;
            this.time        = time;
            this.room        = room;
            this.term        = term;
            this.year        = year;
        }

        public String getCourseCode()  { return courseCode; }
        public String getCourseTitle() { return courseTitle; }
        public String getInstructor()  { return instructor; }
        public String getDay()         { return day; }
        public String getTime()        { return time; }
        public String getRoom()        { return room; }
        public String getTerm()        { return term; }
        public String getYear()        { return year; }

        public String toDisplayText() {
            return courseCode + " — " + courseTitle + "  |  "
                + day + " " + time + "  |  " + instructor + "  |  " + room;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CourseSection that)) return false;
            return courseCode.equals(that.courseCode)
                && instructor.equals(that.instructor)
                && day.equals(that.day)
                && time.equals(that.time)
                && room.equals(that.room)
                && term.equals(that.term)
                && year.equals(that.year);
        }

        @Override
        public int hashCode() {
            return Objects.hash(courseCode, instructor, day, time, room, term, year);
        }
    }
}