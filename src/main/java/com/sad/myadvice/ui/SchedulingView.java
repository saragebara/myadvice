package com.sad.myadvice.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Scheduling screen for student timetable planning.
 *
 * Work Unit Goal:
 * - Help students plan course schedules for a year by selecting sections, instructors,
 *   and times/locations.
 *
 * Users:
 * - Student
 * - Staff
 * - Faculty
 *
 * Use Cases:
 * - Student planning timetable.
 * - Staff projecting computer/lab needs.
 */
public class SchedulingView {

    private final ObservableList<CourseSection> availableSections = FXCollections.observableArrayList();
    private final ObservableList<CourseSection> plannedSections = FXCollections.observableArrayList();
    private final TableView<CourseSection> availableTable = new TableView<>();
    private final ListView<CourseSection> plannedListView = new ListView<>(plannedSections);
    private final Label conflictLabel = new Label();

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UITheme.STYLE_CONTENT_AREA);

        VBox sidebar = buildSidebar();
        VBox content = buildContent();

        root.setLeft(sidebar);
        root.setCenter(content);

        loadAvailableSections();
        refreshConflicts();

        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(UITheme.SPACING);
        sidebar.setStyle(UITheme.STYLE_SIDEBAR);
        sidebar.setPadding(new Insets(UITheme.PAGE_PADDING));

        Label appTitle = new Label("MyAdvice");
        appTitle.setStyle(UITheme.STYLE_SIDEBAR_TITLE);

        Label appSubtitle = new Label("Scheduling");
        appSubtitle.setStyle(UITheme.STYLE_SIDEBAR_SUBTITLE);

        Button scheduleBtn = new Button("Schedule Planner");
        scheduleBtn.setMaxWidth(Double.MAX_VALUE);
        scheduleBtn.setStyle(UITheme.STYLE_SIDEBAR_BUTTON_ACTIVE);

        Button projectedNeedsBtn = new Button("Projected Lab Needs");
        projectedNeedsBtn.setMaxWidth(Double.MAX_VALUE);
        projectedNeedsBtn.setStyle(UITheme.STYLE_SIDEBAR_BUTTON);

        Label usersLabel = new Label("Users: Student, Staff, Faculty");
        usersLabel.setStyle(UITheme.STYLE_SIDEBAR_SUBTITLE);
        usersLabel.setWrapText(true);

        sidebar.getChildren().addAll(appTitle, appSubtitle, scheduleBtn, projectedNeedsBtn, usersLabel);
        return sidebar;
    }

    private VBox buildContent() {
        VBox content = new VBox(UITheme.SPACING);
        content.setPadding(new Insets(UITheme.PAGE_PADDING));

        Label title = new Label("Yearly Course Scheduling");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);

        VBox filtersCard = buildFiltersCard();
        HBox bodyRow = new HBox(UITheme.SPACING, buildAvailableCard(), buildPlannedCard());
        HBox.setHgrow(bodyRow, Priority.ALWAYS);

        VBox.setVgrow(bodyRow, Priority.ALWAYS);
        content.getChildren().addAll(title, goldBar, filtersCard, bodyRow);
        return content;
    }

    private VBox buildFiltersCard() {
        VBox card = new VBox(UITheme.SPACING);
        card.setStyle(UITheme.STYLE_CARD);

        Label section = new Label("Search and Filter");
        section.setStyle(UITheme.STYLE_SECTION_LABEL);

        HBox row = new HBox(UITheme.SPACING);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField courseSearch = new TextField();
        courseSearch.setPromptText("Search by course code/title");
        courseSearch.setStyle(UITheme.STYLE_TEXT_FIELD);

        ComboBox<String> termBox = new ComboBox<>();
        termBox.getItems().addAll("Fall", "Winter", "Spring");
        termBox.setValue("Fall");
        termBox.setStyle(UITheme.STYLE_TEXT_FIELD);

        ComboBox<String> yearBox = new ComboBox<>();
        yearBox.getItems().addAll("2026", "2027");
        yearBox.setValue("2026");
        yearBox.setStyle(UITheme.STYLE_TEXT_FIELD);

        Button applyButton = new Button("Apply");
        applyButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        applyButton.setOnAction(e -> applyFilter(courseSearch.getText(), termBox.getValue(), yearBox.getValue()));

        HBox.setHgrow(courseSearch, Priority.ALWAYS);
        row.getChildren().addAll(courseSearch, termBox, yearBox, applyButton);

        card.getChildren().addAll(section, row);
        return card;
    }

    private VBox buildAvailableCard() {
        VBox card = new VBox(UITheme.SPACING);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPrefWidth(700);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label section = new Label("Available Sections");
        section.setStyle(UITheme.STYLE_SECTION_LABEL);

        TableColumn<CourseSection, String> codeCol = new TableColumn<>("Course");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        TableColumn<CourseSection, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));

        TableColumn<CourseSection, String> instructorCol = new TableColumn<>("Instructor");
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));

        TableColumn<CourseSection, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));

        TableColumn<CourseSection, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<CourseSection, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        availableTable.getColumns().setAll(codeCol, titleCol, instructorCol, dayCol, timeCol, roomCol);
        availableTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableTable.setStyle(UITheme.STYLE_LIST_VIEW);
        availableTable.setItems(availableSections);

        Button addButton = new Button("Add Selected");
        addButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        addButton.setOnAction(e -> addSelectedToPlan());

        VBox.setVgrow(availableTable, Priority.ALWAYS);
        card.getChildren().addAll(section, availableTable, addButton);
        return card;
    }

    private VBox buildPlannedCard() {
        VBox card = new VBox(UITheme.SPACING);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPrefWidth(420);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label section = new Label("Planned Timetable");
        section.setStyle(UITheme.STYLE_SECTION_LABEL);

        plannedListView.setStyle(UITheme.STYLE_LIST_VIEW);
        plannedListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CourseSection item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toDisplayText());
                }
            }
        });

        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);
        removeButton.setOnAction(e -> removeSelectedFromPlan());

        Button saveButton = new Button("Save Plan");
        saveButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        saveButton.setOnAction(e -> savePlannedSchedule());

        conflictLabel.setStyle(UITheme.STYLE_BODY_LABEL);

        HBox actions = new HBox(UITheme.SPACING, removeButton, saveButton);
        VBox.setVgrow(plannedListView, Priority.ALWAYS);
        card.getChildren().addAll(section, plannedListView, actions, conflictLabel);
        return card;
    }

    private void applyFilter(String text, String term, String year) {
        String query = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        List<CourseSection> filtered = getAllSeedSections().stream()
            .filter(s -> s.getTerm().equalsIgnoreCase(term))
            .filter(s -> s.getYear().equals(year))
            .filter(s -> query.isBlank()
                || s.getCourseCode().toLowerCase(Locale.ROOT).contains(query)
                || s.getCourseTitle().toLowerCase(Locale.ROOT).contains(query))
            .sorted(Comparator.comparing(CourseSection::getCourseCode).thenComparing(CourseSection::getDay))
            .collect(Collectors.toList());

        availableSections.setAll(filtered);
    }

    private void addSelectedToPlan() {
        List<CourseSection> selected = new ArrayList<>(availableTable.getSelectionModel().getSelectedItems());
        for (CourseSection section : selected) {
            if (!plannedSections.contains(section)) {
                plannedSections.add(section);
            }
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

    private void refreshConflicts() {
        boolean hasConflict = hasTimeConflict(plannedSections);
        if (hasConflict) {
            conflictLabel.setText("Conflict detected: at least two planned sections overlap.");
        } else {
            conflictLabel.setText("No time conflicts in current timetable.");
        }
    }

    private boolean hasTimeConflict(List<CourseSection> selectedSections) {
        for (int i = 0; i < selectedSections.size(); i++) {
            for (int j = i + 1; j < selectedSections.size(); j++) {
                CourseSection a = selectedSections.get(i);
                CourseSection b = selectedSections.get(j);
                if (a.getDay().equalsIgnoreCase(b.getDay()) && a.getTime().equalsIgnoreCase(b.getTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadAvailableSections() {
        // TODO(Database): Replace this hardcoded list with a repository/service call.
        // Example integration:
        // 1) Query course sections from DB by term/year.
        // 2) Join with instructor and room tables.
        // 3) Map rows into CourseSection DTOs.
        availableSections.setAll(getAllSeedSections().stream()
            .filter(s -> "Fall".equalsIgnoreCase(s.getTerm()) && "2026".equals(s.getYear()))
            .collect(Collectors.toList()));
    }

    private void savePlannedSchedule() {
        // TODO(Database): Persist selected sections to the student's course plan table.
        // Example integration:
        // - Validate prerequisites and seat availability.
        // - Begin transaction.
        // - Insert each chosen section into plan_items.
        // - Commit transaction and return saved plan ID.
        conflictLabel.setText("Plan saved locally (demo mode). " + plannedSections.size() + " sections selected.");
    }

    private List<CourseSection> getAllSeedSections() {
        List<CourseSection> sections = new ArrayList<>();
        sections.add(new CourseSection("CS240", "Data Structures", "Dr. Kim", "Mon", "09:30-10:50", "MC 2017", "Fall", "2026"));
        sections.add(new CourseSection("CS241", "Foundations of Sequential Programs", "Dr. Patel", "Tue", "10:00-11:20", "DC 1350", "Fall", "2026"));
        sections.add(new CourseSection("CS246", "Object-Oriented Software Dev", "Dr. Young", "Wed", "13:00-14:20", "RCH 305", "Fall", "2026"));
        sections.add(new CourseSection("STAT230", "Probability", "Prof. Singh", "Thu", "09:30-10:50", "MC 4042", "Fall", "2026"));
        sections.add(new CourseSection("MATH239", "Intro to Combinatorics", "Prof. Lee", "Fri", "11:30-12:50", "MC 4021", "Fall", "2026"));

        sections.add(new CourseSection("CS245", "Logic and Computation", "Dr. Noor", "Mon", "09:30-10:50", "MC 2034", "Winter", "2027"));
        sections.add(new CourseSection("CS251", "Computer Organization", "Dr. Smith", "Tue", "14:30-15:50", "E7 2409", "Winter", "2027"));
        sections.add(new CourseSection("CS250", "Computer Architecture", "Dr. Brown", "Wed", "10:00-11:20", "E5 6008", "Winter", "2027"));
        sections.add(new CourseSection("CO250", "Intro to Optimization", "Prof. Park", "Thu", "13:00-14:20", "MC 4058", "Winter", "2027"));
        sections.add(new CourseSection("CS241", "Foundations of Sequential Programs", "Dr. Ahmad", "Fri", "09:30-10:50", "DC 1302", "Winter", "2027"));
        return sections;
    }

    public static class CourseSection {
        private final String courseCode;
        private final String courseTitle;
        private final String instructor;
        private final String day;
        private final String time;
        private final String room;
        private final String term;
        private final String year;

        public CourseSection(String courseCode, String courseTitle, String instructor, String day,
                             String time, String room, String term, String year) {
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.instructor = instructor;
            this.day = day;
            this.time = time;
            this.room = room;
            this.term = term;
            this.year = year;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }

        public String getRoom() {
            return room;
        }

        public String getTerm() {
            return term;
        }

        public String getYear() {
            return year;
        }

        public String toDisplayText() {
            return courseCode + " - " + courseTitle + " | " + day + " " + time + " | " + instructor + " | " + room;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CourseSection)) {
                return false;
            }
            CourseSection that = (CourseSection) o;
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
            int result = courseCode.hashCode();
            result = 31 * result + instructor.hashCode();
            result = 31 * result + day.hashCode();
            result = 31 * result + time.hashCode();
            result = 31 * result + room.hashCode();
            result = 31 * result + term.hashCode();
            result = 31 * result + year.hashCode();
            return result;
        }
    }
}
