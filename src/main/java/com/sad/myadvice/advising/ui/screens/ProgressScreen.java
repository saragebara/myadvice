package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.service.CourseService;
import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.service.TranscriptService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgressScreen {

    private final CurriculumService curriculumService;
    private final TranscriptService transcriptService;
    private final CourseService courseService;

    public ProgressScreen(CurriculumService curriculumService, TranscriptService transcriptService,
                          CourseService courseService) {
        this.curriculumService = curriculumService;
        this.transcriptService = transcriptService;
        this.courseService = courseService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Header ----------------------------
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        headerText.getChildren().addAll(
            pageTitle("My Degree Progress"),
            bodyLabel("Logged in as: " + (student != null ? student.getName() : "Unknown"))
        );
        header.getChildren().add(headerText);

        // Completion card --------------------------------
        List<Course> required = courseService.getRequiredCourses();
        double pct = transcriptService.getCompletionPercentage(student, required.size());

        ProgressBar progressBar = new ProgressBar(pct / 100);
        progressBar.setPrefWidth(500);
        progressBar.setStyle(UITheme.STYLE_PROGRESS_BAR);

        Label pctLabel = new Label(String.format("%.1f%% Complete", pct));
        pctLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: "
                + UITheme.UW_BLUE + ";");

        VBox progressCard = new VBox(8, goldBar(), sectionLabel("Degree Completion"),
                progressBar, pctLabel);
        progressCard.setStyle(UITheme.STYLE_CARD);
        progressCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //Remaining required courses card --------------------------------------
        List<Course> remaining = curriculumService.getRemainingRequiredCourses(student);
        ListView<String> remainingList = styledListView(220);

        if (remaining.isEmpty()) {
            remainingList.getItems().add("✓ All required courses completed!");
        } else {
            for (Course c : remaining) {
                remainingList.getItems().add(
                    c.getCode() + "  —  " + c.getName() + "  (Year " + c.getYearLevel() + ")"
                );
            }
        }

        VBox remainingCard = new VBox(8,
            goldBar(),
            sectionLabel("Remaining Required Courses (" + remaining.size() + ")"),
            remainingList
        );
        remainingCard.setStyle(UITheme.STYLE_CARD);
        remainingCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(header, progressCard, remainingCard);
        return view;
    }

    //Helpers

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

    private ListView<String> styledListView(double height) {
        ListView<String> lv = new ListView<>();
        lv.setStyle(UITheme.STYLE_LIST_VIEW);
        lv.setPrefHeight(height);
        return lv;
    }
}