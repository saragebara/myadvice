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
public class FacultyMyAppointmentsScreen {
    //service
    private final BookingService bookingService;
    public FacultyMyAppointmentsScreen(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //main container for faculty to see their upcoming appointments
    public VBox build(User faculty) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("My Appointments"));

        //list view, status and cancel buttons for appointment list 
        ListView<String> apptList = styledListView(280);
        Label statusLabel = new Label("");
        statusLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        Button cancelBtn = secondaryButton("Cancel Selected");
        cancelBtn.setDisable(true);

        //getting the list of upcoing appts
        List<Appointment>[] upcomingRef = new List[]{
            bookingService.getUpcomingForFaculty(faculty)
        };

        //refresh
        Runnable refresh = () -> {
            upcomingRef[0] = bookingService.getUpcomingForFaculty(faculty);
            apptList.getItems().clear(); //clear the list
            if (upcomingRef[0].isEmpty()) { //check if empty, update accordingly
                apptList.getItems().add("No upcoming appointments.");
                cancelBtn.setDisable(true);
            } else { //otherwise add all appointments w student name, reason, and date
                for (Appointment a : upcomingRef[0]) {
                    apptList.getItems().add(
                        a.getStudent().getName() + "  |  " + a.getReasonType() + "  |  " + a.getDateTime()
                    );
                }
            }
        };
        refresh.run();

        //when the list is clicked, allow the faculty to cancel an appointment
        apptList.setOnMouseClicked(e -> {
            int idx = apptList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= upcomingRef[0].size()) return;
            cancelBtn.setDisable(false);
            Appointment selected = upcomingRef[0].get(idx);
            cancelBtn.setOnAction(ev -> { //when click cancel, cancel appt in DB and refresh view
                bookingService.cancelAppointment(selected.getId());
                statusLabel.setText("Appointment cancelled.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                refresh.run();
                cancelBtn.setDisable(true); //disable button
            });
        });

        //main card
        VBox card = new VBox(10,
            goldBar(),
            sectionLabel("Upcoming Confirmed Appointments"),
            apptList,
            cancelBtn,
            statusLabel
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().add(card);
        return view;
    }

    //UI theme imports
    private Region goldBar() { Region bar = new Region(); bar.setStyle(UITheme.STYLE_GOLD_BAR); bar.setMaxWidth(Double.MAX_VALUE); return bar; }
    private Label pageTitle(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private ListView<String> styledListView(double h) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(h); return lv; }
}