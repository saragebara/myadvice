package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EligibleScreen {
    //service
    private final CurriculumService curriculumService;
    public EligibleScreen(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    //main pane
    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Your Eligible Courses"));

        //fixed: now filters using getRequiredCoursesForMajor() and gets req courses based on program
        CurriculumService.EligibleBundle bundle = curriculumService.getEligibleBundle(student);
        List<Course> eligibleRequired  = bundle.eligibleRequired;
        ListView<String> reqList = styledListView(200);

        if (eligibleRequired.isEmpty()) {
            reqList.getItems().add("No eligible required courses at this time.");
        } else {
            for (Course c : eligibleRequired) { //added more context to course details
                String category = c.getCategory() != null ? c.getCategory().toString() : "N/A";
                reqList.getItems().add(
                    c.getCode() + "  —  " + c.getName()
                    + "  (Year " + c.getYearLevel() + ")"
                    + "  [" + category + "]"
                    + "  " + c.getCredits() + " cr"
                );
            }
        }

        //adding components to card
        VBox reqCard = new VBox(8,
            goldBar(),
            sectionLabel("Required Courses (" + eligibleRequired.size() + ")"),
            bodyLabel("Courses required for your degree that you are eligible to take now."),
            reqList
        );//styling
        reqCard.setStyle(UITheme.STYLE_CARD);
        reqCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //bug fix: avoiding leaks
        List<Course> eligibleElectives = bundle.eligibleElectives;
        ListView<String> elecList = styledListView(200);

        //if no eligible electives
        if (eligibleElectives.isEmpty()) {
            elecList.getItems().add("No eligible electives at this time.");
        } else { //otherwise add here
            for (Course c : eligibleElectives) {
                String category = c.getCategory() != null ? c.getCategory().toString() : "N/A";
                elecList.getItems().add(
                    c.getCode() + "  —  " + c.getName()
                    + "  (" + category + ")"
                    + "  " + c.getCredits() + " cr"
                );
            }
        }

        //adding components to elective course card
        VBox elecCard = new VBox(8,
            goldBar(),
            sectionLabel("Elective Courses (" + eligibleElectives.size() + ")"),
            bodyLabel("Optional courses you are eligible to take."),
            elecList
        );
        elecCard.setStyle(UITheme.STYLE_CARD);
        elecCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //adding both cards to main view
        view.getChildren().addAll(reqCard, elecCard);
        return view;
    }

    //helpers------------------
    private Region goldBar() { Region bar = new Region(); bar.setStyle(UITheme.STYLE_GOLD_BAR); bar.setMaxWidth(Double.MAX_VALUE); return bar; }
    private Label pageTitle(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private ListView<String> styledListView(double height) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(height); return lv; }
}