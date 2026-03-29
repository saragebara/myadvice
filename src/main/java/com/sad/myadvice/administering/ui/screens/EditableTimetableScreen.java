package com.sad.myadvice.administering.ui.screens;

import com.sad.myadvice.administering.service.TimetableService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EditableTimetableScreen {

    private final TimetableService timetableService;

    public EditableTimetableScreen(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // Returns VBox to fit inside MainController's contentArea.
    // Add section form is now inline below the table instead of a popup Stage.
    public VBox build() {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Timetable Management");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        // Load data
        ObservableList<Section> sections =
            FXCollections.observableArrayList(timetableService.getAllSections());
        ObservableList<Instructor> instructors =
            FXCollections.observableArrayList(timetableService.getAllInstructors());
        ObservableList<Room> rooms =
            FXCollections.observableArrayList(timetableService.getAllRooms());
        ObservableList<Course> courses =
            FXCollections.observableArrayList(timetableService.getAllCourses());

        // Converters
        StringConverter<Instructor> instrConv = new StringConverter<>() {
            @Override public String toString(Instructor i) { return i == null ? "" : i.getName(); }
            @Override public Instructor fromString(String s) {
                return instructors.stream().filter(i -> i.getName().equals(s)).findFirst().orElse(null);
            }
        };
        StringConverter<Room> roomConv = new StringConverter<>() {
            @Override public String toString(Room r) {
                return r == null ? "" : r.getRoomNumber() + " - " + r.getBuilding();
            }
            @Override public Room fromString(String s) {
                return rooms.stream()
                    .filter(r -> (r.getRoomNumber() + " - " + r.getBuilding()).equals(s))
                    .findFirst().orElse(null);
            }
        };

        // Table
        TableView<Section> table = new TableView<>(sections);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(300);

        TableColumn<Section, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(cd -> {
            Section sec = cd.getValue();
            String name = (sec != null && sec.getCourse() != null)
                ? sec.getCourse().getCode() + " — " + sec.getCourse().getName()
                : "No Course";
            return new SimpleStringProperty(name);
        });

        TableColumn<Section, String> sectionNumCol = new TableColumn<>("Section #");
        sectionNumCol.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getSectionNumber()));
        sectionNumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sectionNumCol.setOnEditCommit(e -> {
            Section sec = e.getRowValue();
            sec.setSectionNumber(e.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, Instructor> instrCol = new TableColumn<>("Instructor");
        instrCol.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(cd.getValue().getInstructor()));
        instrCol.setCellFactory(ComboBoxTableCell.forTableColumn(instrConv, instructors));
        instrCol.setOnEditCommit(e -> {
            Section sec = e.getRowValue();
            sec.setInstructor(e.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, Room> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(cd.getValue().getRoom()));
        roomCol.setCellFactory(ComboBoxTableCell.forTableColumn(roomConv, rooms));
        roomCol.setOnEditCommit(e -> {
            Section sec = e.getRowValue();
            sec.setRoom(e.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getDay()));
        dayCol.setCellFactory(ComboBoxTableCell.forTableColumn(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        dayCol.setOnEditCommit(e -> {
            Section sec = e.getRowValue();
            sec.setDay(e.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        TableColumn<Section, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getTime()));
        timeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        timeCol.setOnEditCommit(e -> {
            Section sec = e.getRowValue();
            sec.setTime(e.getNewValue());
            timetableService.updateSection(sec.getId(), sec);
            table.refresh();
        });

        table.getColumns().addAll(courseCol, sectionNumCol, instrCol, roomCol, dayCol, timeCol);

        // Delete button
        Label tableStatus = bodyLabel("");
        Button deleteBtn = secondaryButton("Delete Selected Section");
        deleteBtn.setOnAction(e -> {
            Section selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                tableStatus.setText("Please select a section to delete.");
                tableStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            Alert confirm = new Alert(AlertType.CONFIRMATION,
                "Delete section " + selected.getSectionNumber() + "?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        timetableService.deleteSection(selected.getId());
                        sections.setAll(timetableService.getAllSections());
                        table.refresh();
                        tableStatus.setText("✓ Section deleted.");
                        tableStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
                    } catch (Exception ex) {
                        tableStatus.setText("✗ " + ex.getMessage());
                        tableStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                    }
                }
            });
        });

        HBox tableButtons = new HBox(10, deleteBtn);
        tableButtons.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(10,
            goldBar(),
            sectionLabel("Current Sections"),
            bodyLabel("Double-click a cell to edit. Use dropdowns for instructor, room, and day."),
            table,
            tableButtons,
            tableStatus
        );
        tableCard.setStyle(UITheme.STYLE_CARD);
        tableCard.setPadding(new Insets(UITheme.CARD_PADDING));

        // --- Add section form (inline, no popup) ---
        ComboBox<Course> courseBox = new ComboBox<>(courses);
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        courseBox.setConverter(new StringConverter<>() {
            @Override public String toString(Course c) {
                return c == null ? "" : c.getCode() + " — " + c.getName();
            }
            @Override public Course fromString(String s) { return null; }
        });

        TextField sectionNumField = new TextField();
        sectionNumField.setPromptText("Section Number (e.g. 01)");
        sectionNumField.setStyle(UITheme.STYLE_TEXT_FIELD);
        sectionNumField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Instructor> instructorBox = new ComboBox<>(instructors);
        instructorBox.setPromptText("Select Instructor");
        instructorBox.setMaxWidth(Double.MAX_VALUE);
        instructorBox.setConverter(new StringConverter<>() {
            @Override public String toString(Instructor i) { return i == null ? "" : i.getName(); }
            @Override public Instructor fromString(String s) { return null; }
        });

        ComboBox<Room> roomBox = new ComboBox<>(rooms);
        roomBox.setPromptText("Select Room");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        roomBox.setConverter(new StringConverter<>() {
            @Override public String toString(Room r) {
                return r == null ? "" : r.getRoomNumber() + " - " + r.getBuilding();
            }
            @Override public Room fromString(String s) { return null; }
        });

        ComboBox<String> dayBox = new ComboBox<>();
        dayBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        dayBox.setPromptText("Select Day");
        dayBox.setMaxWidth(Double.MAX_VALUE);

        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g. 10:00 AM - 11:30 AM)");
        timeField.setStyle(UITheme.STYLE_TEXT_FIELD);
        timeField.setMaxWidth(Double.MAX_VALUE);

        Label addStatus = bodyLabel("");
        Button addBtn = primaryButton("Add Section");

        addBtn.setOnAction(e -> {
            if (courseBox.getValue() == null || instructorBox.getValue() == null
                    || roomBox.getValue() == null || dayBox.getValue() == null
                    || sectionNumField.getText().isBlank() || timeField.getText().isBlank()) {
                addStatus.setText("Please fill in all fields.");
                addStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
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
                // Refresh table and reset form
                sections.setAll(timetableService.getAllSections());
                table.refresh();
                courseBox.setValue(null);
                sectionNumField.clear();
                instructorBox.setValue(null);
                roomBox.setValue(null);
                dayBox.setValue(null);
                timeField.clear();
                addStatus.setText("✓ Section added.");
                addStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            } catch (Exception ex) {
                addStatus.setText("✗ " + ex.getMessage());
                addStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }
        });

        // Two-column form layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        form.getColumnConstraints().addAll(col1, col2);

        form.add(bodyLabel("Course"),      0, 0); form.add(courseBox,      0, 1);
        form.add(bodyLabel("Section #"),   1, 0); form.add(sectionNumField, 1, 1);
        form.add(bodyLabel("Instructor"),  0, 2); form.add(instructorBox,   0, 3);
        form.add(bodyLabel("Room"),        1, 2); form.add(roomBox,         1, 3);
        form.add(bodyLabel("Day"),         0, 4); form.add(dayBox,          0, 5);
        form.add(bodyLabel("Time"),        1, 4); form.add(timeField,       1, 5);

        VBox addCard = new VBox(10,
            goldBar(),
            sectionLabel("Add New Section"),
            form,
            addBtn,
            addStatus
        );
        addCard.setStyle(UITheme.STYLE_CARD);
        addCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(title, tableCard, addCard);
        return view;
    }

    private Region goldBar() { Region b = new Region(); b.setStyle(UITheme.STYLE_GOLD_BAR); b.setMaxWidth(Double.MAX_VALUE); return b; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
}