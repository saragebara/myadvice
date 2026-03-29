package com.sad.myadvice.administering.ui;

import com.sad.myadvice.entity.AdminCourse;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import com.sad.myadvice.administering.service.CourseService;
import com.sad.myadvice.administering.service.TimetableService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class EditableTimetableScreen {

    private final TimetableService timetableService;
    private final CourseService courseService;

    public EditableTimetableScreen(TimetableService timetableService, CourseService courseService) {
        this.timetableService = timetableService;
        this.courseService = courseService;
    }

    public void show(Stage stage) {
        //layout container
        VBox root = new VBox(UITheme.SPACING);
        root.setPadding(new Insets(UITheme.PAGE_PADDING));
        root.setStyle(UITheme.STYLE_CONTENT_AREA);
        Label title = new Label("Editable Timetable");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //table setup
        TableView<Section> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ObservableList<Section> sections =
            FXCollections.observableArrayList(timetableService.getAllSections()); //load data
        table.setItems(sections);
        //data for dropdowns
        ObservableList<Instructor> instructors =
            FXCollections.observableArrayList(timetableService.getAllInstructors());
        ObservableList<Room> rooms =
            FXCollections.observableArrayList(timetableService.getAllRooms());
        ObservableList<AdminCourse> courses =
            FXCollections.observableArrayList(courseService.getAllCourses());
        //converters for dropdowns
        StringConverter<Instructor> instructorConverter = new StringConverter<>() {
            @Override
            public String toString(Instructor instructor) {
                return instructor == null ? "" : instructor.getName();
            }

            @Override
            public Instructor fromString(String string) {
                for (Instructor i : instructors) {
                    if (i.getName().equals(string)) {
                        return i;
                    }
                }
                return null;
            }
        };
        StringConverter<Room> roomConverter = new StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room == null ? "" : room.getRoomNumber() + " - " + room.getBuilding();
            }

            @Override
            public Room fromString(String string) {
                for (Room r : rooms) {
                    String label = r.getRoomNumber() + " - " + r.getBuilding();
                    if (label.equals(string)) {
                        return r;
                    }
                }
                return null;
            }
        };

        //course name (read-only)
        TableColumn<Section, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(cellData -> {
            Section sec = cellData.getValue();
            String courseName = "No Course";
            if (sec != null && sec.getCourse() != null) {
                courseName = sec.getCourse().getCourseName();
            }
            return new SimpleStringProperty(courseName);
        });

        //editable columns
        TableColumn<Section, String> sectionNumCol = new TableColumn<>("Section #");
        sectionNumCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getSectionNumber()));
        sectionNumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sectionNumCol.setOnEditCommit(event -> {
            Section sec = event.getRowValue();
            sec.setSectionNumber(event.getNewValue());
            timetableService.updateSection(sec.getId(), sec);   //update database
            table.refresh();
        });

        TableColumn<Section, Instructor> instructorCol = new TableColumn<>("Instructor");
        instructorCol.setCellValueFactory(cellData ->
            new SimpleObjectProperty<>(cellData.getValue().getInstructor()));
        instructorCol.setCellFactory(ComboBoxTableCell.forTableColumn(instructorConverter, instructors));
        instructorCol.setOnEditCommit(event -> {
            Section sec = event.getRowValue();
            sec.setInstructor(event.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, Room> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(cellData ->
            new SimpleObjectProperty<>(cellData.getValue().getRoom()));
        roomCol.setCellFactory(ComboBoxTableCell.forTableColumn(roomConverter, rooms));
        roomCol.setOnEditCommit(event -> {
            Section sec = event.getRowValue();
            sec.setRoom(event.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDay()));
        dayCol.setCellFactory(ComboBoxTableCell.forTableColumn(
            "Monday", "Tuesday","Wednesday", "Thursday", "Friday"
        ));
        dayCol.setOnEditCommit(event -> {
            Section sec = event.getRowValue();
            sec.setDay(event.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTime()));
        timeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        timeCol.setOnEditCommit(event -> {
            Section sec = event.getRowValue();
            sec.setTime(event.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        table.getColumns().addAll(courseCol, sectionNumCol, instructorCol, roomCol, dayCol, timeCol);
        //add sectiojn and delete section
        Button addSectionBtn = new Button("Add Section");
        addSectionBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        addSectionBtn.setOnAction(e ->
            showAddSectionDialog(stage, table, courses, instructors, rooms)
        );

        Button deleteSectionBtn = new Button("Delete Section");
        deleteSectionBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);
        deleteSectionBtn.setOnAction(e -> {
            Section selected = table.getSelectionModel().getSelectedItem();
            //null exceptions bc my code keeps breaking without them
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Section Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a section to delete.");
                alert.showAndWait();
                return;
            }

            //are you sure? button to be fancy
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Delete Section");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete section " + selected.getSectionNumber() + "?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        timetableService.deleteSection(selected.getId());
                        table.setItems(FXCollections.observableArrayList(timetableService.getAllSections()));
                        table.refresh();
                    } catch (Exception ex) {
                        Alert error = new Alert(AlertType.ERROR);
                        error.setTitle("Delete Failed");
                        error.setHeaderText(null);
                        error.setContentText(ex.getMessage());
                        error.showAndWait();
                    }
                }
            });
        });

        //add buttons for adding and deleting
        HBox buttonBar = new HBox(UITheme.SPACING, addSectionBtn, deleteSectionBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(title, table, buttonBar);

        //set scene
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Admin Editable Timetable");
        stage.show();
    }

    private void showAddSectionDialog(Stage owner,TableView<Section> table,ObservableList<AdminCourse> courses, ObservableList<Instructor> instructors, ObservableList<Room> rooms) {
        //popup window
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Section");

        //poopup layout
        VBox root = new VBox(UITheme.SPACING);
        root.setPadding(new Insets(UITheme.PAGE_PADDING));
        root.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Add Section");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //dropdown and dropdown controlls
        ComboBox<AdminCourse> courseBox = new ComboBox<>(courses);
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        courseBox.setConverter(new StringConverter<>() {
            @Override
            //more null checks 
            public String toString(AdminCourse course) {
                return course == null ? "" : course.getCourseCode() + " - " + course.getCourseName();
            }

            @Override
            public AdminCourse fromString(String string) {
                return null;
            }
        });

        //input and dropdown
        TextField sectionNumField = new TextField();
        sectionNumField.setPromptText("Section Number");
        sectionNumField.setStyle(UITheme.STYLE_TEXT_FIELD);

        ComboBox<Instructor> instructorBox = new ComboBox<>(instructors);
        instructorBox.setPromptText("Select Instructor");
        instructorBox.setMaxWidth(Double.MAX_VALUE);
        instructorBox.setConverter(new StringConverter<>() {
            @Override
            //more null checks 
            public String toString(Instructor instructor) {
                return instructor == null ? "" : instructor.getName();
            }

            @Override
            public Instructor fromString(String string) {
                return null;
            }
        });

        ComboBox<Room> roomBox = new ComboBox<>(rooms);
        roomBox.setPromptText("Select Room");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        roomBox.setConverter(new StringConverter<>() {
            @Override
            //more null checks 
            public String toString(Room room) {
                return room == null ? "" : room.getRoomNumber() + " - " + room.getBuilding();
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });

        ComboBox<String> dayBox = new ComboBox<>();
        dayBox.getItems().addAll("Monday", "Tuesday","Wednesday", "Thursday", "Friday");
        dayBox.setPromptText("Select Day");
        dayBox.setMaxWidth(Double.MAX_VALUE);

        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g. 10:00 AM)");
        timeField.setStyle(UITheme.STYLE_TEXT_FIELD);

        //saving and cancling buttons
        Button saveBtn = new Button("Save");
        saveBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        saveBtn.setOnAction(e -> {
            //MORE NULL CHECKS!!! w/try and catch too
            if (courseBox.getValue() == null || instructorBox.getValue() == null || roomBox.getValue() == null ||
                dayBox.getValue() == null || sectionNumField.getText().isBlank() || timeField.getText().isBlank()){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Missing Information");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields.");
                alert.showAndWait();
                return;
            }

            try {
                Section newSection = new Section();
                newSection.setSectionNumber(sectionNumField.getText().trim());
                newSection.setDay(dayBox.getValue());
                newSection.setTime(timeField.getText().trim());
                timetableService.createSection(
                    courseBox.getValue().getId(),
                    instructorBox.getValue().getId(),
                    roomBox.getValue().getId(),
                    newSection
                );

                table.setItems(FXCollections.observableArrayList(timetableService.getAllSections()));
                table.refresh();
                dialog.close();

            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Could Not Add Section");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());
        //button layout
        HBox buttonBar = new HBox(UITheme.SPACING, saveBtn, cancelBtn);

        root.getChildren().addAll(title, courseBox, sectionNumField,instructorBox, roomBox, dayBox, timeField, buttonBar);
        Scene scene = new Scene(root, 420, 420);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}