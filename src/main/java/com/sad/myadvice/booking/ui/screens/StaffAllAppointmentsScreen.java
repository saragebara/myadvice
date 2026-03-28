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

//Separate from report analytics -- shows DETAILED pane of all current appointments
@Component
public class StaffAllAppointmentsScreen {
    //service
    private final BookingService bookingService;
    public StaffAllAppointmentsScreen(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //main pane to show all appointments (NOT EDITABLE)
    public VBox build(User staff) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("All Appointments (Read-Only)"));
        //getting all appts
        List<Appointment> all = bookingService.getAllAppointments();
        //creating list view of appts
        ListView<String> apptList = styledListView(400);
        if (all.isEmpty()) { //if empty list
            apptList.getItems().add("No appointments in the system yet.");
        } else { //otherwise add appointments to the list view
            for (Appointment a : all) {
                apptList.getItems().add(
                    a.getStudent().getName() + "  →  " +
                    a.getFaculty().getName() + "  |  " +
                    a.getReasonType() + "  |  " +
                    a.getDateTime() + "  |  " +
                    a.getStatus()
                );
            }
        }

        //card for all appointments in the system
        VBox card = new VBox(10,
            goldBar(),
            sectionLabel("All System Appointments (" + all.size() + ")"),
            bodyLabel("Read-only view. Contact faculty directly to modify appointments."),
            apptList
        );
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().add(card);
        return view;
    }

    //UI styling
    private Region goldBar() { Region bar = new Region(); bar.setStyle(UITheme.STYLE_GOLD_BAR); bar.setMaxWidth(Double.MAX_VALUE); return bar; }
    private Label pageTitle(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private ListView<String> styledListView(double h) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(h); return lv; }
}