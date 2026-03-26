package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.service.CourseService;
import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
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
public class CourseDetailsScreen {
    private final CourseService courseService;
    private final CurriculumService curriculumService;

    public CourseDetailsScreen(CourseService courseService, CurriculumService curriculumService) {
        this.courseService = courseService;
        this.curriculumService = curriculumService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Course Details"));

        //Search bar ------------------------------
        TextField searchField = styledTextField("Search by course code or name...");
        searchField.setPrefWidth(320);
        Button searchBtn = primaryButton("Search");
        Button resetBtn = secondaryButton("Reset");
        //adding everything to a horiziontal bar
        HBox searchBar = new HBox(10, searchField, searchBtn, resetBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        //Results list ------------------------------
        ListView<String> resultsList = styledListView(140);
        //keeping track of last results
        final List<Course>[] lastResults = new List[]{List.of()};

        //Details panel ------------------------------ (when clicking a course)
        VBox detailsPanel = new VBox(8);
        detailsPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        detailsPanel.getChildren().add(
            bodyLabel("Select a course from the search results to see details.")
        );

        //Search button action ----------------------------------
        searchBtn.setOnAction(e -> {
            resultsList.getItems().clear(); //clearing results list
            List<Course> results = courseService.searchCourses(searchField.getText());
            lastResults[0] = results; //storing the courses that match
            if (results.isEmpty()) {
                resultsList.getItems().add("No courses found.");
            } else {
                for (Course c : results) { //add proper courses to results
                    resultsList.getItems().add(c.getCode() + "  —  " + c.getName());
                }
            }
        });

        // Reset buttion action ---------------------------
        resetBtn.setOnAction(e -> {
            searchField.clear(); //clearing search field
            resultsList.getItems().clear(); //clearing results
            lastResults[0] = List.of(); //empty list
            detailsPanel.getChildren().setAll( //prompting user again
                bodyLabel("Select a course from the search results to see details.")
            );
        });

        //Click result to see details -----------------------
        resultsList.setOnMouseClicked(e -> {
            int idx = resultsList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= lastResults[0].size()) return;
            Course selected = lastResults[0].get(idx);

            //checking if user is eligible to take the course and saving messages accordingly
            boolean eligible = curriculumService.isEligible(student, selected);
            String eligibleStr = eligible ? "✓ Yes" : "✗ No - prerequisites not met";
            String eligibleColor = eligible ? "#2E7D32" : "#C62828";

            Label eligibleLabel = new Label("Eligible to take: " + eligibleStr);
            eligibleLabel.setStyle( //javafx styling for the label 
                "-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + eligibleColor + ";"
            );

            detailsPanel.getChildren().setAll(
                goldBar(),
                boldLabel(selected.getCode() + "  —  " + selected.getName()),
                bodyLabel("Credits: " + selected.getCredits()),
                bodyLabel("Year Level: " + selected.getYearLevel()),
                bodyLabel("Required: " + (selected.isRequired() ? "Yes" : "No")),
                bodyLabel("Category: " + (selected.getCategory() != null
                        ? selected.getCategory().toString() : "N/A")),
                eligibleLabel
            );
        });

        //Wrapping everything in a search card -----------------
        VBox searchCard = new VBox(10,
            goldBar(),
            sectionLabel("Search Courses"),
            searchBar,
            resultsList,
            sectionLabel("Course Details"),
            detailsPanel
        );
        searchCard.setStyle(UITheme.STYLE_CARD);
        searchCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().add(searchCard);
        return view;
    }

    // Helpers for bar, title, section label, body label, bold label, buttons, text field, list view
    //-----------------------------------------------------------------------------------------------
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
        return tf;
    }

    private ListView<String> styledListView(double height) {
        ListView<String> lv = new ListView<>();
        lv.setStyle(UITheme.STYLE_LIST_VIEW);
        lv.setPrefHeight(height);
        return lv;
    }
    //-----------------------------------------------------------------------------------------------
}