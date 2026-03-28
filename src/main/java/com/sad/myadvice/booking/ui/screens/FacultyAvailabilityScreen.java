package com.sad.myadvice.booking.ui.screens;

import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.booking.service.AvailabilityService;
import com.sad.myadvice.entity.AvailabilitySlot;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
public class FacultyAvailabilityScreen {
    //service
    private final AvailabilityService availabilityService;
    public FacultyAvailabilityScreen(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    //main pane for faculty to manage their availability for appointment slots
    public VBox build(User faculty) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Manage My Availability"));

        //current slots
        ListView<String> slotsList = styledListView(200);
        Label statusLabel = new Label("");
        statusLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        Button removeBtn = secondaryButton("Remove Selected Slot");
        removeBtn.setDisable(true);
        //getting list of slots
        List<AvailabilitySlot>[] slotsRef = new List[]{
            availabilityService.getSlotsForFaculty(faculty)
        };

        //refresh
        Runnable refresh = () -> {
            slotsRef[0] = availabilityService.getSlotsForFaculty(faculty); //getting slots
            slotsList.getItems().clear(); //clearing list that user sees
            if (slotsRef[0].isEmpty()) { //if slots are empty
                slotsList.getItems().add("No availability slots set.");
                removeBtn.setDisable(true);
            } else { //otherwise check if the slot is recurring or just a specific date
                for (AvailabilitySlot slot : slotsRef[0]) {
                    String s = slot.isRecurring()
                        ? slot.getDayOfWeek() + "  " + slot.getStartTime() + " – " + slot.getEndTime() + "  (Weekly)"
                        : slot.getSpecificDate() + "  " + slot.getStartTime() + " – " + slot.getEndTime() + "  (One-off)";
                    slotsList.getItems().add(s);
                }
            }
        };
        refresh.run();

        //when click a slot on the list
        slotsList.setOnMouseClicked(e -> {
            int idx = slotsList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= slotsRef[0].size()) return;
            removeBtn.setDisable(false); //enable remove button
            AvailabilitySlot selected = slotsRef[0].get(idx);
            removeBtn.setOnAction(ev -> { //if user removes a slot, get rid of it and refresh view
                availabilityService.removeSlot(selected.getId());
                statusLabel.setText("Slot removed.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                refresh.run();
                removeBtn.setDisable(true);
            });
        });

        //main content pane for current available slots
        VBox currentCard = new VBox(10,
            goldBar(),
            sectionLabel("Current Availability Slots"),
            slotsList,
            removeBtn,
            statusLabel
        ); //UI Theme
        currentCard.setStyle(UITheme.STYLE_CARD);
        currentCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //adding a new slot
        //combo box with days of the week (styled)
        ComboBox<DayOfWeek> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll(DayOfWeek.values());
        dayCombo.setValue(DayOfWeek.MONDAY);
        dayCombo.setStyle(UITheme.STYLE_TEXT_FIELD);
        dayCombo.setMaxWidth(Double.MAX_VALUE);

        //combo box for the start and end of the slots
        ComboBox<String> startCombo = new ComboBox<>();
        ComboBox<String> endCombo = new ComboBox<>();
        for (int h = 8; h <= 17; h++) {
            startCombo.getItems().addAll( //formatting
                String.format("%02d:00", h), String.format("%02d:30", h));
            endCombo.getItems().addAll(
                String.format("%02d:00", h), String.format("%02d:30", h));
        }
        startCombo.setValue("09:00"); //initial values
        endCombo.setValue("10:00");
        startCombo.setStyle(UITheme.STYLE_TEXT_FIELD); //styling
        endCombo.setStyle(UITheme.STYLE_TEXT_FIELD);
        startCombo.setMaxWidth(Double.MAX_VALUE);
        endCombo.setMaxWidth(Double.MAX_VALUE);
        //status label that gets updated
        Label addStatus = new Label("");
        addStatus.setStyle(UITheme.STYLE_BODY_LABEL);

        //adding a recurring weekly appointment slot
        Button addBtn = primaryButton("Add Recurring Weekly Slot");
        addBtn.setOnAction(e -> { //when clicked, parse the start/end time
            LocalTime start = LocalTime.parse(startCombo.getValue());
            LocalTime end = LocalTime.parse(endCombo.getValue());
            if (!end.isAfter(start)) { //if not possible, warn the user in status label
                addStatus.setText("⚠ End time must be after start time.");
                addStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            //otherwise update DB. setting max capacity as 1 by default 
            availabilityService.addRecurringSlot(faculty, dayCombo.getValue(), start, end, 1);
            addStatus.setText("✓ Slot added successfully!");
            addStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            refresh.run();
        });

        //time row for combo boxes
        HBox timeRow = new HBox(10, startCombo, new Label("to"), endCombo);
        timeRow.setAlignment(Pos.CENTER_LEFT);

        //main card for adding slots
        VBox addCard = new VBox(10,
            goldBar(),
            sectionLabel("Add New Availability Slot"),
            new Label("Day of Week") {{ setStyle(UITheme.STYLE_BODY_LABEL); }},
            dayCombo,
            new Label("Time Range") {{ setStyle(UITheme.STYLE_BODY_LABEL); }},
            timeRow,
            addBtn,
            addStatus
        ); //styling
        addCard.setStyle(UITheme.STYLE_CARD);
        addCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(currentCard, addCard);
        return view;
    }

    //UI theme imports
    private Region goldBar() { Region bar = new Region(); bar.setStyle(UITheme.STYLE_GOLD_BAR); bar.setMaxWidth(Double.MAX_VALUE); return bar; }
    private Label pageTitle(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private ListView<String> styledListView(double h) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(h); return lv; }
}