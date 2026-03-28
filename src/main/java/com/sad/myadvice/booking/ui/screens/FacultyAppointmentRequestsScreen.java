package com.sad.myadvice.booking.ui.screens;

import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.booking.service.BookingService;
import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacultyAppointmentRequestsScreen {
    //service
    private final BookingService bookingService;
    public FacultyAppointmentRequestsScreen(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //main container for faculty to see incoming appointment requests
    public VBox build(User faculty) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Appointment Requests"));

        ListView<String> requestsList = styledListView(260);
        Label detailsLabel = new Label("Select a request to view details.");
        detailsLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        detailsLabel.setWrapText(true);

        //confirm/reject incoming appointments
        Button confirmBtn = primaryButton("✓ Confirm");
        Button rejectBtn = secondaryButton("✗ Reject");
        confirmBtn.setDisable(true);
        rejectBtn.setDisable(true);
        Label statusLabel = new Label("");
        statusLabel.setStyle(UITheme.STYLE_BODY_LABEL);

        HBox actionBar = new HBox(10, confirmBtn, rejectBtn);

        List<Appointment>[] pendingRef = new List[]{
            bookingService.getPendingForFaculty(faculty)
        };

        //refresh to keep checking for appointments and update the pane
        Runnable refresh = () -> {
            pendingRef[0] = bookingService.getPendingForFaculty(faculty);
            requestsList.getItems().clear();
            if (pendingRef[0].isEmpty()) {
                requestsList.getItems().add("No pending requests.");
                confirmBtn.setDisable(true); //can't confirm or reject if there are no appts
                rejectBtn.setDisable(true);
            } else {
                for (Appointment a : pendingRef[0]) {
                    requestsList.getItems().add( //otherwise add each appointmentn with student  name, reason, and date
                        a.getStudent().getName() + "  |  " + a.getReasonType() + "  |  " + a.getDateTime().toLocalDate()
                    );
                }
            }
        };
        refresh.run();

        //when faculty clicks a request, it shows detailed information about the appointment
        requestsList.setOnMouseClicked(e -> {
            int idx = requestsList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= pendingRef[0].size()) return;
            Appointment selected = pendingRef[0].get(idx);
            detailsLabel.setText(
                "Student: " + selected.getStudent().getName() +
                "\nReason: " + selected.getReasonType() +
                "\nDate: " + selected.getDateTime() +
                "\nNote: " + (selected.getNote() != null ? selected.getNote() : "None")
            );
            confirmBtn.setDisable(false); //allowing confirm/reject now
            rejectBtn.setDisable(false);
            
            //when faculty confirms an appointment
            confirmBtn.setOnAction(ev -> {
                bookingService.confirmAppointment(selected.getId()); //set appt as confirmed
                statusLabel.setText("✓ Appointment confirmed."); //confirmation
                statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
                refresh.run(); //refresh appointment list
                detailsLabel.setText("Select a request to view details."); //update label
                confirmBtn.setDisable(true); //disable confirm/reject appointments
                rejectBtn.setDisable(true);
            });

            //when faculty rejects an appointment
            rejectBtn.setOnAction(ev -> {
                bookingService.rejectAppointment(selected.getId());
                statusLabel.setText("✗ Appointment rejected."); //set appt as rejected
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                refresh.run(); //refresh appt list and labels and buttons
                detailsLabel.setText("Select a request to view details.");
                confirmBtn.setDisable(true);
                rejectBtn.setDisable(true);
            });
        });

        //card to see pending requests and request details
        VBox card = new VBox(10,
            goldBar(),
            sectionLabel("Pending Requests (" + pendingRef[0].size() + ")"),
            requestsList,
            sectionLabel("Request Details"),
            detailsLabel,
            actionBar,
            statusLabel
        );
        card.setStyle(UITheme.STYLE_CARD); //imported themes 
        card.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().add(card);
        return view;
    }

    //pretty gold bar so that its not so ugly to look at
    private Region goldBar() {
        Region bar = new Region();
        bar.setStyle(UITheme.STYLE_GOLD_BAR);
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }
    //setting ui theme for everything used here. so its not ugly. yay
    private Label pageTitle(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private ListView<String> styledListView(double h) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(h); return lv; }
}