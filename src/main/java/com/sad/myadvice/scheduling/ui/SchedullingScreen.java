package com.sad.myadvice.scheduling.ui;

import com.sad.myadvice.advising.service.TranscriptService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class SchedullingScreen {

    private final TranscriptService transcriptService;

    public SchedullingScreen(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = pageTitle("Schedulling");
        Label subtitle = bodyLabel(
            "Build a yearly schedule using your transcript progress and available sections."
        );
        subtitle.setWrapText(true);

        HBox cards = new HBox(UITheme.SPACING);
        cards.getChildren().addAll(buildTranscriptContextCard(student), buildPlannerCard(student));
        HBox.setHgrow(cards, Priority.ALWAYS);

        view.getChildren().addAll(title, subtitle, cards);
        return view;
    }

    private VBox buildTranscriptContextCard(User student) {
        Label section = sectionLabel("Transcript Context");

        ListView<String> completedList = new ListView<>();
        completedList.setStyle(UITheme.STYLE_LIST_VIEW);
        completedList.setPrefHeight(180);

        ListView<String> inProgressList = new ListView<>();
        inProgressList.setStyle(UITheme.STYLE_LIST_VIEW);
        inProgressList.setPrefHeight(180);

        // Backend connection point:
        // Data is sourced from advising/service/TranscriptService.java.
        // TranscriptService already calls TranscriptRepository, so this screen can use it directly
        // to show what the student has completed and what is currently in progress.
        List<Course> completedCourses = transcriptService.getCompletedCourses(student);
        List<Course> inProgressCourses = transcriptService.getInProgressCourses(student);

        completedList.setItems(FXCollections.observableArrayList(formatCourses(completedCourses)));
        inProgressList.setItems(FXCollections.observableArrayList(formatCourses(inProgressCourses)));

        if (completedCourses.isEmpty()) {
            completedList.getItems().add("No completed courses found.");
        }
        if (inProgressCourses.isEmpty()) {
            inProgressList.getItems().add("No in-progress courses found.");
        }

        VBox card = new VBox(10,
            goldBar(),
            section,
            bodyLabel("Completed Courses (from TranscriptService)"), completedList,
            bodyLabel("In-Progress Courses (from TranscriptService)"), inProgressList
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(430);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildPlannerCard(User student) {
        Label section = sectionLabel("Yearly Planner");

        TextField searchField = new TextField();
        searchField.setPromptText("Search section by course code or title");
        searchField.setStyle(UITheme.STYLE_TEXT_FIELD);

        ObservableList<String> availableSections = FXCollections.observableArrayList(seedSections());
        ObservableList<String> selectedSections = FXCollections.observableArrayList();

        ListView<String> availableList = new ListView<>(availableSections);
        availableList.setStyle(UITheme.STYLE_LIST_VIEW);
        availableList.setPrefHeight(180);

        ListView<String> selectedList = new ListView<>(selectedSections);
        selectedList.setStyle(UITheme.STYLE_LIST_VIEW);
        selectedList.setPrefHeight(180);

        Label statusLabel = bodyLabel("Select a section and add it to your schedule.");
        statusLabel.setWrapText(true);

        Button addBtn = new Button("Add Section");
        addBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button removeBtn = new Button("Remove Section");
        removeBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        Button saveBtn = new Button("Save Schedule");
        saveBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.trim().toLowerCase(Locale.ROOT);
            List<String> filtered = seedSections().stream()
                .filter(s -> query.isBlank() || s.toLowerCase(Locale.ROOT).contains(query))
                .toList();
            availableSections.setAll(filtered);
        });

        addBtn.setOnAction(e -> {
            String selected = availableList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Please select a section first.");
                return;
            }
            if (!selectedSections.contains(selected)) {
                selectedSections.add(selected);
            }
            statusLabel.setText("Section added to schedule.");
        });

        removeBtn.setOnAction(e -> {
            String selected = selectedList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Select a scheduled section to remove.");
                return;
            }
            selectedSections.remove(selected);
            statusLabel.setText("Section removed.");
        });

        saveBtn.setOnAction(e -> {
            // Backend connection point:
            // Create a SchedulingService in advising/service and persist to a schedule table.
            // Suggested flow:
            // 1) Validate selected sections against TranscriptService prerequisite status.
            // 2) Write schedule rows via SchedulingRepository.
            // 3) Link saved rows to student ID (student.getId() / student.getStudentId()).
            // 4) Return saved schedule id and show confirmation.
            statusLabel.setText("Schedule saved in demo mode for " + (student == null ? "student" : student.getStudentId()) + ".");
        });

        HBox actions = new HBox(10, addBtn, removeBtn, saveBtn);

        VBox card = new VBox(10,
            goldBar(),
            section,
            bodyLabel("Available Sections (hardcoded for now)"),
            searchField,
            availableList,
            bodyLabel("Selected Sections"),
            selectedList,
            actions,
            statusLabel
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(520);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    private List<String> formatCourses(List<Course> courses) {
        List<String> rows = new ArrayList<>();
        for (Course course : courses) {
            // Backend note: this depends on fields from entity/Course.java.
            // If fields are renamed, update this formatter only.
            rows.add(course.getCode() + " - " + course.getName());
        }
        return rows;
    }

    private List<String> seedSections() {
        List<String> rows = new ArrayList<>();
        rows.add("COMP2800 A | Mon 09:30-10:50 | Dr. Ahmed | Erie Hall 210");
        rows.add("COMP3030 B | Tue 11:30-12:50 | Dr. Campbell | Lambton 101");
        rows.add("COMP3100 C | Wed 13:00-14:20 | Dr. Taylor | Essex 220");
        rows.add("COMP4000 D | Thu 10:00-11:20 | Prof. Morgan | Erie Hall 120");
        rows.add("COMP4050 E | Fri 14:30-15:50 | Prof. Singh | Erie Hall 310");
        return rows;
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
}
