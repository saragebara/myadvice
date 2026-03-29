package com.sad.myadvice.administering.ui.screens;

import com.sad.myadvice.administering.service.AdminCourseService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Prerequisite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EditablePrerequisitesScreen {

    private final AdminCourseService adminCourseService;

    public EditablePrerequisitesScreen(AdminCourseService adminCourseService) {
        this.adminCourseService = adminCourseService;
    }

    // Returns VBox to fit inside MainController's contentArea like all other screens
    public VBox build() {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Edit Course Prerequisites");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        // Course table
        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(280);

        ObservableList<Course> courses =
            FXCollections.observableArrayList(adminCourseService.getAllCourses());
        table.setItems(courses);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(cd.getValue().getCode()));

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(cd.getValue().getName()));

        TableColumn<Course, String> prereqCol = new TableColumn<>("Current Prerequisites");
        prereqCol.setCellValueFactory(cd -> {
            List<Prerequisite> prereqs =
                adminCourseService.getPrerequisitesForCourse(cd.getValue().getId());
            String text = prereqs.isEmpty() ? "None" : prereqs.stream()
                .map(p -> p.getRequiredCourse().getCode() + " (" + p.getType() + ")")
                .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        table.getColumns().addAll(codeCol, nameCol, prereqCol);

        // Edit panel — shown inline below the table when a course is selected
        VBox editPanel = new VBox(UITheme.SPACING);
        editPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        editPanel.getChildren().add(bodyLabel("Select a course above to edit its prerequisites."));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected == null) return;
            buildEditPanel(editPanel, selected, table, courses);
        });

        // Button editBtn = primaryButton("Edit Selected Course");
        // editBtn.setOnAction(e -> {
        //     Course selected = table.getSelectionModel().getSelectedItem();
        //     if (selected == null) {
        //         new Alert(AlertType.WARNING, "Please select a course first.").showAndWait();
        //         return;
        //     }
        //     buildEditPanel(editPanel, selected, table, courses);
        // });

        // HBox buttonBar = new HBox(UITheme.SPACING, editBtn);
        // buttonBar.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(10, goldBar(), sectionLabel("Courses"), table);
        tableCard.setStyle(UITheme.STYLE_CARD);
        tableCard.setPadding(new Insets(UITheme.CARD_PADDING));

        VBox editCard = new VBox(10, goldBar(), sectionLabel("Edit Prerequisites"), editPanel);
        editCard.setStyle(UITheme.STYLE_CARD);
        editCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(title, tableCard, editCard);
        return view;
    }

    private void buildEditPanel(VBox panel, Course selectedCourse,
                                 TableView<Course> table,
                                 ObservableList<Course> courses) {
        panel.getChildren().clear();

        panel.getChildren().add(boldLabel(
            "Editing: " + selectedCourse.getCode() + " — " + selectedCourse.getName()));

        List<Prerequisite> existingPrereqs =
            adminCourseService.getPrerequisitesForCourse(selectedCourse.getId());
        Map<Long, Long> existingByTargetId = new HashMap<>();
        for (Prerequisite p : existingPrereqs) {
            existingByTargetId.put(p.getRequiredCourse().getId(), p.getId());
        }

        List<Course> allCourses = adminCourseService.getAllCourses();
        VBox checkboxContainer = new VBox(6);
        List<CheckBox> checkBoxes = new ArrayList<>();
        List<ComboBox<Prerequisite.Type>> typeBoxes = new ArrayList<>();
        List<Course> candidateCourses = new ArrayList<>();

        for (Course course : allCourses) {
            if (course.getId().equals(selectedCourse.getId())) continue;

            CheckBox cb = new CheckBox(course.getCode() + " — " + course.getName());
            cb.setStyle(UITheme.STYLE_BODY_LABEL);

            ComboBox<Prerequisite.Type> typeBox = new ComboBox<>();
            typeBox.getItems().addAll(Prerequisite.Type.values());
            typeBox.setStyle(UITheme.STYLE_TEXT_FIELD);
            typeBox.disableProperty().bind(cb.selectedProperty().not());

            if (existingByTargetId.containsKey(course.getId())) {
                cb.setSelected(true);
                existingPrereqs.stream()
                    .filter(p -> p.getRequiredCourse().getId().equals(course.getId()))
                    .findFirst()
                    .ifPresent(p -> typeBox.setValue(p.getType()));
            } else {
                typeBox.setValue(Prerequisite.Type.PRE);
            }

            HBox row = new HBox(10, cb, typeBox);
            row.setAlignment(Pos.CENTER_LEFT);
            checkboxContainer.getChildren().add(row);
            checkBoxes.add(cb);
            typeBoxes.add(typeBox);
            candidateCourses.add(course);
        }

        ScrollPane scrollPane = new ScrollPane(checkboxContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);

        Label statusLabel = bodyLabel("");

        Button saveBtn = primaryButton("Save Changes");
        Button cancelBtn = secondaryButton("Cancel");

        saveBtn.setOnAction(e -> {
            try {
                for (int i = 0; i < checkBoxes.size(); i++) {
                    Course candidate = candidateCourses.get(i);
                    boolean isChecked = checkBoxes.get(i).isSelected();
                    Prerequisite.Type type = typeBoxes.get(i).getValue();
                    boolean wasExisting = existingByTargetId.containsKey(candidate.getId());

                    if (!isChecked && wasExisting) {
                        adminCourseService.removePrerequisite(
                            existingByTargetId.get(candidate.getId()));
                    } else if (isChecked && !wasExisting) {
                        adminCourseService.addPrerequisite(
                            selectedCourse.getId(), candidate.getId(), type);
                    }
                }
                // Refresh table
                courses.setAll(adminCourseService.getAllCourses());
                table.refresh();
                statusLabel.setText("✓ Prerequisites updated.");
                statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            } catch (Exception ex) {
                statusLabel.setText("✗ " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }
        });

        cancelBtn.setOnAction(e -> {
            panel.getChildren().setAll(
                bodyLabel("Select a course above to edit its prerequisites."));
        });

        HBox buttonBar = new HBox(10, saveBtn, cancelBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        panel.getChildren().addAll(scrollPane, buttonBar, statusLabel);
    }

    private Region goldBar() { Region b = new Region(); b.setStyle(UITheme.STYLE_GOLD_BAR); b.setMaxWidth(Double.MAX_VALUE); return b; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Label boldLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL + " -fx-font-weight: bold;"); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
}