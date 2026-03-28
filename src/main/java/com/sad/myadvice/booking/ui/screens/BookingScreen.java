package com.sad.myadvice.booking.ui.screens;

import com.sad.myadvice.booking.service.AvailabilityService;
import com.sad.myadvice.booking.service.BookingService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.AvailabilitySlot;
import com.sad.myadvice.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BookingScreen {

    private final BookingService bookingService;
    private final AvailabilityService availabilityService;

    public BookingScreen(BookingService bookingService, AvailabilityService availabilityService) {
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = pageTitle("Faculty Booking");
        Label subtitle = bodyLabel(
            "Find a faculty advisor for curriculum support, COMP 400/405 supervision, or research advising."
        );
        subtitle.setWrapText(true);

        HBox cards = new HBox(UITheme.SPACING);
        cards.getChildren().addAll(
            buildFacultyFinderCard(student),
            buildRequestCard(student)
        );
        HBox.setHgrow(cards, Priority.ALWAYS);

        view.getChildren().addAll(title, subtitle, cards);
        return view;
    }

    //card to find faculty and show their details
    private VBox buildFacultyFinderCard(User student) {
        Label section = sectionLabel("Faculty Availability");

        //loading all faculty from DB
        List<User> allFaculty = bookingService.getAllFaculty();
        //showing them as a list
        ObservableList<String> facultyRows = FXCollections.observableArrayList();
        ListView<String> facultyList = new ListView<>(facultyRows);
        facultyList.setStyle(UITheme.STYLE_LIST_VIEW);
        facultyList.setPrefHeight(260);
        //populating list with faculty names and emails
        for (User faculty : allFaculty) {
            facultyRows.add(faculty.getName() + "  |  " + faculty.getEmail());
        }
        //if there's no faculty members
        if (allFaculty.isEmpty()) {
            facultyRows.add("No faculty found in the system.");
        }

        //Details panel ---------------------------------------------------------
        VBox detailsPanel = new VBox(8);
        detailsPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        detailsPanel.getChildren().add(bodyLabel("Select a faculty member to view their available slots."));

        //Click faculty to see availabilty ---------------------------------------------------------
        facultyList.setOnMouseClicked(e -> {
            int idx = facultyList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= allFaculty.size()) return;
            User selectedFaculty = allFaculty.get(idx); //selected faculty based on click
            //list of available slots for the selected faculty member
            List<AvailabilitySlot> slots = availabilityService.getSlotsForFaculty(selectedFaculty);

            //clearing details panel and showing selected faculty's details
            detailsPanel.getChildren().clear();
            detailsPanel.getChildren().add(boldLabel(selectedFaculty.getName()));
            detailsPanel.getChildren().add(bodyLabel("Email: " + selectedFaculty.getEmail()));

            //if no availability
            if (slots.isEmpty()) {
                detailsPanel.getChildren().add(bodyLabel("No availability slots set."));
            } else { //otherwise show availability
                detailsPanel.getChildren().add(sectionLabel("Available Slots:"));
                for (AvailabilitySlot slot : slots) {
                    String slotStr = slot.isRecurring()
                        ? slot.getDayOfWeek() + "  " + slot.getStartTime() + " – " + slot.getEndTime() + "  (Weekly)"
                        : slot.getSpecificDate() + "  " + slot.getStartTime() + " – " + slot.getEndTime() + "  (One-off)";
                    detailsPanel.getChildren().add(bodyLabel("• " + slotStr));
                }
            }
        });

        //VBox to store this card at the left
        VBox card = new VBox(10, goldBar(), section, facultyList, detailsPanel);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(460);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    //Booking request card ---------------------------------------------------------
    private VBox buildRequestCard(User student) {
        Label section = sectionLabel("Book an Appointment");

        //faculty selection 
        List<User> allFaculty = bookingService.getAllFaculty();
        ComboBox<String> facultyCombo = new ComboBox<>();
        for (User f : allFaculty) {
            facultyCombo.getItems().add(f.getName() + " — " + f.getEmail());
        }
        facultyCombo.setPromptText("Select a faculty member...");
        facultyCombo.setStyle(UITheme.STYLE_TEXT_FIELD);
        facultyCombo.setMaxWidth(Double.MAX_VALUE);

        //student name field
        TextField nameField = styledTextField("Student Full Name");
        if (student != null) {
            nameField.setText(student.getName());
        }

        //student id field
        TextField idField = styledTextField("Student ID");
        if (student != null) {
            idField.setText(student.getStudentId());
        }

        //reason type combo box
        ComboBox<String> reasonCombo = new ComboBox<>();
        reasonCombo.getItems().addAll("CURRICULUM", "COMP400", "RESEARCH", "GENERAL");
        reasonCombo.setValue("CURRICULUM");
        reasonCombo.setStyle(UITheme.STYLE_TEXT_FIELD);
        reasonCombo.setMaxWidth(Double.MAX_VALUE);

        //date picker
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setStyle(UITheme.STYLE_TEXT_FIELD);
        datePicker.setMaxWidth(Double.MAX_VALUE);

        //time selector
        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.getItems().addAll("09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00");
        timeCombo.setValue("10:00");
        timeCombo.setStyle(UITheme.STYLE_TEXT_FIELD);
        timeCombo.setMaxWidth(Double.MAX_VALUE);

        //notes about appointment
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Describe your advising question...");
        noteArea.setStyle(UITheme.STYLE_TEXT_FIELD);
        noteArea.setWrapText(true);
        noteArea.setPrefRowCount(4);

        Label statusLabel = bodyLabel("");

        Button submitBtn = primaryButton("Submit Request");
        Button clearBtn = secondaryButton("Clear");

        //when user clicks submit:
        submitBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || idField.getText().trim().isEmpty()) {
                statusLabel.setText("Please fill in student name and student ID before submitting.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            int facultyIdx = facultyCombo.getSelectionModel().getSelectedIndex();
            if (facultyIdx < 0) {
                statusLabel.setText("⚠ Please select a faculty member.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            if (datePicker.getValue() == null) {
                statusLabel.setText("⚠ Please select a date.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }

            User selectedFaculty = allFaculty.get(facultyIdx);
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeCombo.getValue(),
                DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            Appointment.ReasonType reason = Appointment.ReasonType.valueOf(reasonCombo.getValue());

            bookingService.bookAppointment(
                student,
                selectedFaculty,
                dateTime,
                reason,
                noteArea.getText().trim()
            );

            statusLabel.setText("✓ Appointment request submitted successfully!");
            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            noteArea.clear();
        });

        //clear button action
        clearBtn.setOnAction(e -> {
            facultyCombo.getSelectionModel().clearSelection();
            reasonCombo.setValue("CURRICULUM");
            datePicker.setValue(LocalDate.now().plusDays(1));
            timeCombo.setValue("10:00");
            noteArea.clear();
            statusLabel.setText("");
        });

        HBox actionBar = new HBox(10, submitBtn, clearBtn);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10,
            goldBar(),
            section,
            sectionLabel("Faculty Member"), facultyCombo,
            sectionLabel("Your Name"), nameField,
            sectionLabel("Student ID"), idField,
            sectionLabel("Appointment Purpose"), reasonCombo,
            sectionLabel("Preferred Date"), datePicker,
            sectionLabel("Preferred Time"), timeCombo,
            sectionLabel("Notes"), noteArea,
            actionBar,
            statusLabel
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(460);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    //helpers ---------------------------------------------------------

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

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle(UITheme.STYLE_BODY_LABEL + " -fx-font-weight: bold;");
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

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(UITheme.STYLE_TEXT_FIELD);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }
}