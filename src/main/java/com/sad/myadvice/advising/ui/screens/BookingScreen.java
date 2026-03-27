package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.model.AdvisorProfile;
import com.sad.myadvice.advising.model.BookingRequest;
import com.sad.myadvice.advising.service.BookingService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BookingScreen {

    private final BookingService bookingService;

    public BookingScreen(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = pageTitle("Academic Booking");
        Label subtitle = bodyLabel(
            "Work Unit Goal: help students find the right faculty advisor for curriculum/timetable support, "
                + "COMP 400/405 supervision, and graduate research advising."
        );
        subtitle.setWrapText(true);

        HBox cards = new HBox(UITheme.SPACING);
        cards.getChildren().addAll(buildFacultyFinderCard(), buildRequestCard(student));
        HBox.setHgrow(cards, Priority.ALWAYS);

        view.getChildren().addAll(title, subtitle, cards);
        return view;
    }

    private VBox buildFacultyFinderCard() {
        Label section = sectionLabel("Academic Advisor Availability");

        ComboBox<String> topicFilter = new ComboBox<>();
        topicFilter.getItems().addAll("All Topics", "Curriculum", "Timetable", "COMP 400/405", "Research");
        topicFilter.setValue("All Topics");
        topicFilter.setStyle(UITheme.STYLE_TEXT_FIELD);
        topicFilter.setMaxWidth(Double.MAX_VALUE);

        ObservableList<String> advisorRows = FXCollections.observableArrayList();
        ListView<String> advisorList = new ListView<>(advisorRows);
        advisorList.setStyle(UITheme.STYLE_LIST_VIEW);
        advisorList.setPrefHeight(300);

        Label details = bodyLabel("Select an advisor to view details.");
        details.setWrapText(true);
        VBox detailsPanel = new VBox(8, sectionLabel("Advisor Details"), details);
        detailsPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);

        Runnable refreshList = () -> {
            advisorRows.clear();
            List<AdvisorProfile> advisors = bookingService.findAdvisorsByTopic(topicFilter.getValue());
            for (AdvisorProfile advisor : advisors) {
                advisorRows.add(advisor.getAdvisorName() + "  |  " + advisor.getAdvisingTopic() + "  |  " + advisor.getAvailability());
            }
            if (advisors.isEmpty()) {
                advisorRows.add("No faculty found for this advising topic.");
            }
            details.setText("Select an advisor to view details.");
        };

        topicFilter.setOnAction(e -> refreshList.run());

        advisorList.setOnMouseClicked(e -> {
            int index = advisorList.getSelectionModel().getSelectedIndex();
            List<AdvisorProfile> advisors = bookingService.findAdvisorsByTopic(topicFilter.getValue());
            if (index < 0 || index >= advisors.size()) {
                return;
            }
            AdvisorProfile selected = advisors.get(index);
            details.setText(
                "Name: " + selected.getAdvisorName() + "\n"
                    + "Department: " + selected.getDepartment() + "\n"
                    + "Topic: " + selected.getAdvisingTopic() + "\n"
                    + "Availability: " + selected.getAvailability() + "\n"
                    + "Office Hours: " + selected.getOfficeHours()
            );
        });

        refreshList.run();

        VBox card = new VBox(10, goldBar(), section, topicFilter, advisorList, detailsPanel);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(460);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildRequestCard(User student) {
        Label section = sectionLabel("Student Appointment Request");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Student", "Staff", "Faculty");
        roleBox.setValue("Student");
        roleBox.setStyle(UITheme.STYLE_TEXT_FIELD);
        roleBox.setMaxWidth(Double.MAX_VALUE);

        TextField nameField = styledTextField("Student Full Name");
        if (student != null) {
            nameField.setText(student.getName());
        }

        TextField idField = styledTextField("Student ID");
        if (student != null) {
            idField.setText(student.getStudentId());
        }

        ComboBox<String> purposeBox = new ComboBox<>();
        purposeBox.getItems().addAll("Curriculum", "Timetable", "COMP 400/405", "Research");
        purposeBox.setValue("Curriculum");
        purposeBox.setStyle(UITheme.STYLE_TEXT_FIELD);
        purposeBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> advisorBox = new ComboBox<>();
        advisorBox.setStyle(UITheme.STYLE_TEXT_FIELD);
        advisorBox.setMaxWidth(Double.MAX_VALUE);

        Runnable refreshAdvisorChoices = () -> {
            List<AdvisorProfile> advisors = bookingService.findAdvisorsByTopic(purposeBox.getValue());
            advisorBox.getItems().setAll(advisors.stream().map(AdvisorProfile::getAdvisorName).toList());
            if (!advisorBox.getItems().isEmpty()) {
                advisorBox.setValue(advisorBox.getItems().get(0));
            } else {
                advisorBox.setValue(null);
            }
        };
        purposeBox.setOnAction(e -> refreshAdvisorChoices.run());
        refreshAdvisorChoices.run();

        DatePicker datePicker = new DatePicker(nextSchoolDay(LocalDate.now()));
        datePicker.setStyle(UITheme.STYLE_TEXT_FIELD);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setDisable(false);
                    return;
                }
                boolean isPast = item.isBefore(LocalDate.now());
                boolean isWeekend = item.getDayOfWeek().getValue() >= 6;
                setDisable(isPast || isWeekend);
            }
        });

        ComboBox<String> timeBox = new ComboBox<>();
        timeBox.getItems().addAll("09:00", "10:00", "11:30", "13:00", "14:30", "16:00");
        timeBox.setValue("10:00");
        timeBox.setStyle(UITheme.STYLE_TEXT_FIELD);
        timeBox.setMaxWidth(Double.MAX_VALUE);

        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Describe your advising question...");
        detailsArea.setStyle(UITheme.STYLE_TEXT_FIELD);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(5);

        Label statusLabel = bodyLabel("");

        Runnable clearFormFields = () -> {
            roleBox.setValue("Student");
            nameField.clear();
            idField.clear();
            purposeBox.setValue("Curriculum");
            refreshAdvisorChoices.run();
            datePicker.setValue(nextSchoolDay(LocalDate.now()));
            timeBox.setValue("10:00");
            detailsArea.clear();
        };

        Button submitBtn = primaryButton("Submit Request");
        Button clearBtn = secondaryButton("Clear");

        submitBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || idField.getText().trim().isEmpty()) {
                statusLabel.setText("Please fill in student name and student ID before submitting.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            if (advisorBox.getValue() == null || advisorBox.getValue().trim().isEmpty()) {
                statusLabel.setText("Please select an advisor before submitting.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }

            BookingRequest request = new BookingRequest(
                roleBox.getValue(),
                nameField.getText().trim(),
                idField.getText().trim(),
                purposeBox.getValue(),
                advisorBox.getValue(),
                datePicker.getValue(),
                timeBox.getValue(),
                detailsArea.getText().trim()
            );

            try {
                String bookingId = bookingService.submitBooking(request);
                clearFormFields.run();
                statusLabel.setText("Request submitted successfully. Booking ID: " + bookingId);
                statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }
        });

        clearBtn.setOnAction(e -> {
            clearFormFields.run();
            statusLabel.setText("");
        });

        HBox actionBar = new HBox(10, submitBtn, clearBtn);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10,
            goldBar(),
            section,
            bodyLabel("Users: Student, Staff, Faculty"),
            bodyLabel("Requester Role"), roleBox,
            bodyLabel("Student Name"), nameField,
            bodyLabel("Student ID"), idField,
            bodyLabel("Appointment Purpose"), purposeBox,
            bodyLabel("Advisor"), advisorBox,
            bodyLabel("Preferred Date"), datePicker,
            bodyLabel("Preferred Time"), timeBox,
            bodyLabel("Question Details"), detailsArea,
            actionBar,
            statusLabel
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));
        card.setPrefWidth(460);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
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

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(UITheme.STYLE_TEXT_FIELD);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
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

    private LocalDate nextSchoolDay(LocalDate from) {
        LocalDate date = from;
        while (date.getDayOfWeek().getValue() >= 6) {
            date = date.plusDays(1);
        }
        return date;
    }
}