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

    private final CurriculumService curriculumService;

    public EligibleScreen(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Your Eligible Courses"));

        //Eligible courses that are **required** ---------------------------
        List<Course> eligibleRequired = curriculumService.getEligibleRequired(student);
        ListView<String> reqList = styledListView(170);

        if (eligibleRequired.isEmpty()) {
            reqList.getItems().add("No eligible required courses at this time.");
        } else {
            for (Course c : eligibleRequired) {
                reqList.getItems().add(
                    c.getCode() + "  —  " + c.getName() + "  (Year " + c.getYearLevel() + ")"
                );
            }
        }

        //Card for eligible required courses 
        VBox reqCard = new VBox(8,
            goldBar(),
            sectionLabel("Required Courses (" + eligibleRequired.size() + ")"),
            reqList
        );
        reqCard.setStyle(UITheme.STYLE_CARD);
        reqCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //Eligible courses that are **electives** ---------------------------
        List<Course> eligibleElectives = curriculumService.getEligibleElectives(student);
        ListView<String> elecList = styledListView(170);

        if (eligibleElectives.isEmpty()) {
            elecList.getItems().add("No eligible electives at this time.");
        } else {
            for (Course c : eligibleElectives) {
                elecList.getItems().add(
                    c.getCode() + "  —  " + c.getName() + "  (" + c.getCategory() + ")"
                );
            }
        }

        //card for electives
        VBox elecCard = new VBox(8,
            goldBar(),
            sectionLabel("Elective Courses (" + eligibleElectives.size() + ")"),
            elecList
        );
        elecCard.setStyle(UITheme.STYLE_CARD);
        elecCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(reqCard, elecCard);
        return view;
    }

    //Helpers: gold bar, page ttile, lable, list view --------------------------
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

    private ListView<String> styledListView(double height) {
        ListView<String> lv = new ListView<>();
        lv.setStyle(UITheme.STYLE_LIST_VIEW);
        lv.setPrefHeight(height);
        return lv;
    }
    //----------------------------------------------------------------------------
}