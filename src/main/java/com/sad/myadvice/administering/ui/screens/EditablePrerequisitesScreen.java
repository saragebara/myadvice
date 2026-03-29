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

    public VBox build() {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Edit Course Prerequisites");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //query 1 for all courses
        List<Course> allCoursesList = adminCourseService.getAllCourses();

        //query 2 all prerequisites
        Map<Long, List<Prerequisite>> prereqMap = adminCourseService.getAllPrerequisites()
            .stream()
            .collect(Collectors.groupingBy(p -> p.getCourse().getId()));

        //ObservableList backed by the already-loaded list, got rid of extra query
        ObservableList<Course> courses = FXCollections.observableArrayList(allCoursesList);

        //Table -----
        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(280);
        table.setItems(courses);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(cd.getValue().getCode()));

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(cd.getValue().getName()));

        TableColumn<Course, String> prereqCol = new TableColumn<>("Current Prerequisites");
        prereqCol.setCellValueFactory(cd -> {
            //in-memory lookup = zero DB calls per row
            List<Prerequisite> prereqs =
                prereqMap.getOrDefault(cd.getValue().getId(), List.of());
            String text = prereqs.isEmpty() ? "None" : prereqs.stream()
                .map(p -> p.getRequiredCourse().getCode() + " (" + p.getType() + ")")
                .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        table.getColumns().addAll(codeCol, nameCol, prereqCol);

        //Edit panel --------
        VBox editPanel = new VBox(UITheme.SPACING);
        editPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        editPanel.getChildren().add(bodyLabel("Select a course above to edit its prerequisites."));

        //pass the shared prereqMap and allCoursesList down to avoid requerying inside
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected == null) return;
            buildEditPanel(editPanel, selected, table, courses, allCoursesList, prereqMap);
        });

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
                                 ObservableList<Course> courses,
                                 List<Course> allCoursesList,
                                 Map<Long, List<Prerequisite>> prereqMap) {
        panel.getChildren().clear();
        panel.getChildren().add(boldLabel("Editing: " + selectedCourse.getCode() + " — " + selectedCourse.getName()));

        //no more DB call, replaced w in-memory data
        List<Prerequisite> existingPrereqs =
            prereqMap.getOrDefault(selectedCourse.getId(), List.of());
        Map<Long, Long> existingByTargetId = new HashMap<>();
        for (Prerequisite p : existingPrereqs) {
            existingByTargetId.put(p.getRequiredCourse().getId(), p.getId());
        }

        VBox checkboxContainer = new VBox(6);
        List<CheckBox> checkBoxes = new ArrayList<>();
        List<ComboBox<Prerequisite.Type>> typeBoxes = new ArrayList<>();
        List<Course> candidateCourses = new ArrayList<>();

        //uses the already-loaded allCoursesList now
        for (Course course : allCoursesList) {
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
                        //update in-memory map to reflect deletion
                        prereqMap.getOrDefault(selectedCourse.getId(), new ArrayList<>())
                            .removeIf(p -> p.getId().equals(existingByTargetId.get(candidate.getId())));
                        existingByTargetId.remove(candidate.getId());
                    } else if (isChecked && !wasExisting) {
                        Prerequisite saved = adminCourseService.addPrerequisite(
                            selectedCourse.getId(), candidate.getId(), type);
                        //update in-memory map to reflect addition
                        prereqMap.computeIfAbsent(selectedCourse.getId(), k -> new ArrayList<>())
                            .add(saved);
                        existingByTargetId.put(candidate.getId(), saved.getId());
                    }
                }

                //refresh table display using in-memory map
                table.refresh();
                statusLabel.setText("✓ Prerequisites updated.");
                statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            } catch (Exception ex) {
                statusLabel.setText("✗ " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }
        });

        cancelBtn.setOnAction(e ->
            panel.getChildren().setAll(
                bodyLabel("Select a course above to edit its prerequisites.")));

        HBox buttonBar = new HBox(10, saveBtn, cancelBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        panel.getChildren().addAll(scrollPane, buttonBar, statusLabel);
    }

    //Helpers ------------
    private Region goldBar() { Region b = new Region(); b.setStyle(UITheme.STYLE_GOLD_BAR); b.setMaxWidth(Double.MAX_VALUE); return b; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Label boldLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL + " -fx-font-weight: bold;"); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
}