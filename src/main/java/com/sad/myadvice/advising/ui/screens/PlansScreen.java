package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.service.CoursePlanService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlansScreen {
    private final CoursePlanService coursePlanService;

    public PlansScreen(CoursePlanService coursePlanService) {
        this.coursePlanService = coursePlanService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("My Course Plans"));

        //Create new plan-------------------------------------
        TextField planNameField = styledTextField("Enter a name for your new plan...");
        planNameField.setPrefWidth(300);
        Button createBtn = primaryButton("Create Plan");
        Label statusLabel = new Label("");
        statusLabel.setStyle(UITheme.STYLE_BODY_LABEL);

        HBox createBar = new HBox(10, planNameField, createBtn);
        createBar.setAlignment(Pos.CENTER_LEFT);

        //Plans list------------------------------------------
        ListView<String> plansList = styledListView(220);

        //Load plans helper-------------------------------------
        Runnable refreshPlans = () -> {
            plansList.getItems().clear();
            List<CoursePlan> plans = coursePlanService.getPlansForStudent(student);
            if (plans.isEmpty()) {
                plansList.getItems().add("No plans yet — create one above!");
            } else {
                for (CoursePlan p : plans) {
                    String status = p.isApproved() ? "  ✓ Approved" : "  — Pending approval";
                    plansList.getItems().add(p.getPlanName() + status);
                }
            }
        };
        refreshPlans.run();

        //Create plan action --------------------------------------
        createBtn.setOnAction(e -> {
            String name = planNameField.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("⚠ Please enter a plan name.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            coursePlanService.createPlan(student, name);
            planNameField.clear();
            statusLabel.setText("✓ Plan \"" + name + "\" created successfully!");
            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            refreshPlans.run();
        });

        //Card
        VBox plansCard = new VBox(10,
            goldBar(),
            sectionLabel("Create a New Plan"),
            createBar,
            statusLabel,
            sectionLabel("My Saved Plans"),
            plansList
        );
        plansCard.setStyle(UITheme.STYLE_CARD);
        plansCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().add(plansCard);
        return view;
    }

    //Helpers ---------------------------------------------

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

    private Button primaryButton(String text) {
        Button b = new Button(text);
        b.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        return b;
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(UITheme.STYLE_TEXT_FIELD);
        return tf;
    }

    private ListView<String> styledListView(double height) {
        ListView<String> lv = new ListView<>();
        lv.setStyle(UITheme.STYLE_LIST_VIEW);
        lv.setPrefHeight(height);
        return lv;
    }
}