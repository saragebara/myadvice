package com.sad.myadvice.administering.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sad.myadvice.entity.AdminCourse;
import com.sad.myadvice.administering.service.CourseService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditablePrerequisitesScreen {

    private final CourseService courseService;

    public EditablePrerequisitesScreen(CourseService courseService) {
        this.courseService = courseService;
    }

    public void show(Stage stage) {
        //layout container
        VBox root = new VBox(UITheme.SPACING);
        root.setPadding(new Insets(UITheme.PAGE_PADDING));
        root.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Edit Course Prerequisites");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //table setup
        TableView<AdminCourse> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        ObservableList<AdminCourse> courses =
            FXCollections.observableArrayList(courseService.getAllCourses()); //load data
        table.setItems(courses);

        //basic columns
        TableColumn<AdminCourse, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        TableColumn<AdminCourse, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        //shows current prereqs
        TableColumn<AdminCourse, String> prereqCol = new TableColumn<>("Current Prerequisites");
        prereqCol.setCellValueFactory(cellData -> {
            AdminCourse course = cellData.getValue();

            String prereqText = "None";
            if (course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
                prereqText = course.getPrerequisites()
                    .stream()
                    .map(AdminCourse::getCourseCode)
                    .collect(Collectors.joining(", "));
            }

            return new javafx.beans.property.SimpleStringProperty(prereqText);
        });

        table.getColumns().addAll(codeCol, nameCol, prereqCol);

        //button for editing prereqs
        Button editBtn = new Button("Edit Prerequisites");
        editBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        editBtn.setOnAction(e -> {
            AdminCourse selected = table.getSelectionModel().getSelectedItem();

            //null exceptions everywhere!!!
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Course Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a course to edit its prerequisites.");
                alert.showAndWait();
                return;
            }
            showEditPrerequisitesDialog(stage, table, selected);
        });

        //button layout
        HBox buttonBar = new HBox(UITheme.SPACING, editBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(title, table, buttonBar);

        //set scene
        Scene scene = new Scene(root, 950, 600);
        stage.setScene(scene);
        stage.setTitle("Admin Course Prerequisites");
        stage.show();
    }

    private void showEditPrerequisitesDialog(Stage owner, TableView<AdminCourse> table, AdminCourse selectedCourse) {
        //popup window
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Prerequisites");

        //popup layout
        VBox root = new VBox(UITheme.SPACING);
        root.setPadding(new Insets(UITheme.PAGE_PADDING));
        root.setStyle(UITheme.STYLE_CONTENT_AREA);

        //title with current selected course
        Label title = new Label("Edit Prerequisites for " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName());
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //get all courses for checkbox list
        List<AdminCourse> allCourses = courseService.getAllCourses();

        //stores ids of existing prereqs
        Set<Long> existingPrereqIds = new HashSet<>();
        if (selectedCourse.getPrerequisites() != null) {
            for (AdminCourse prereq : selectedCourse.getPrerequisites()) {
                existingPrereqIds.add(prereq.getId());
            }
        }

        //container for checkboxes
        VBox checkboxContainer = new VBox(UITheme.SPACING);
        List<CheckBox> checkBoxes = new ArrayList<>();
        List<AdminCourse> candidateCourses = new ArrayList<>();

        for (AdminCourse course : allCourses) {
            if (course.getId().equals(selectedCourse.getId())) {
                continue; //cannot be its own prerequisite
            }

            String label = course.getCourseCode() + " - " + course.getCourseName();
            CheckBox cb = new CheckBox(label);
            cb.setStyle(UITheme.STYLE_BODY_LABEL);

            //checks already existing prereqs
            if (existingPrereqIds.contains(course.getId())) {
                cb.setSelected(true);
            }

            checkBoxes.add(cb);
            candidateCourses.add(course);
            checkboxContainer.getChildren().add(cb);
        }

        //scroll area
        ScrollPane scrollPane = new ScrollPane(checkboxContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);

        //saving and cancling buttons
        Button saveBtn = new Button("Save");
        saveBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        saveBtn.setOnAction(e -> {
            try {
                Set<Long> checkedIds = new HashSet<>();

                //collect ids of everything currently checked
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        checkedIds.add(candidateCourses.get(i).getId());
                    }
                }

                //remove prerequisites that were unchecked
                for (Long existingId : existingPrereqIds) {
                    if (!checkedIds.contains(existingId)) {
                        courseService.removePrerequisite(selectedCourse.getId(), existingId);
                    }
                }

                //add prerequisites that were newly checked
                for (Long checkedId : checkedIds) {
                    if (!existingPrereqIds.contains(checkedId)) {
                        courseService.addPrerequisite(selectedCourse.getId(), checkedId);
                    }
                }

                //reload table after changes
                table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
                table.refresh();
                dialog.close();

            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Could Not Update Prerequisites");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        //button layout
        HBox buttonBar = new HBox(UITheme.SPACING, saveBtn, cancelBtn);
        root.getChildren().addAll(title, scrollPane, buttonBar);

        //set scene
        Scene scene = new Scene(root, 600, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}