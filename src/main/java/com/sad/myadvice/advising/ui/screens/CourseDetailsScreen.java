package com.sad.myadvice.advising.ui.screens;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sad.myadvice.advising.service.CourseService;
import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.PrerequisiteRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@Component
public class CourseDetailsScreen {
    //service/repository
    private final CourseService courseService;
    private final CurriculumService curriculumService;
    private final PrerequisiteRepository prerequisiteRepository;
    public CourseDetailsScreen(CourseService courseService,
                               CurriculumService curriculumService,
                               PrerequisiteRepository prerequisiteRepository) {
        this.courseService = courseService;
        this.curriculumService = curriculumService;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Course Details"));

        //Search bar -------------------------------------
        TextField searchField = styledTextField("Search by course code or name...");
        searchField.setPrefWidth(320);
        Button searchBtn = primaryButton("Search");
        Button resetBtn = secondaryButton("Reset");
        //adding everything to a horiziontal bar
        HBox searchBar = new HBox(10, searchField, searchBtn, resetBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        //Results list -----------------
        ListView<String> resultsList = styledListView(150);
        final List<Course>[] lastResults = new List[]{List.of()};

        //Details panel
        VBox detailsPanel = new VBox(10);
        detailsPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        detailsPanel.getChildren().add(bodyLabel("Select a course from the search results to see details."));

        //Search button action
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            resultsList.getItems().clear();
            detailsPanel.getChildren().setAll(bodyLabel("Select a course to see details."));

            if (query.isEmpty()) {
                lastResults[0] = List.of();
                return;
            }

            List<Course> results = courseService.searchCourses(query);
            lastResults[0] = results;

            if (results.isEmpty()) {
                resultsList.getItems().add("No courses found.");
            } else {
                for (Course c : results) {
                    //fixed: use isReqForMajor instead of global requiremnet flag
                    boolean reqForMajor = curriculumService.isRequiredForMajor(student, c);
                    String reqTag = reqForMajor ? " [Required]" : " [Elective]";
                    resultsList.getItems().add(
                        c.getCode() + "  —  " + c.getName() + "  (" + c.getCredits() + " cr)" + reqTag
                    );
                }
            }
        });
        //allow enter key to search
        searchField.setOnAction(e -> searchBtn.fire());

        //reset button action
        resetBtn.setOnAction(e -> {
            searchField.clear();
            resultsList.getItems().clear();
            lastResults[0] = List.of();
            detailsPanel.getChildren().setAll(
                bodyLabel("Select a course from the search results to see details.")
            );
        });

        resultsList.setOnMouseClicked(e -> {
            int idx = resultsList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= lastResults[0].size()) return;

            Course selected = lastResults[0].get(idx);
            populateDetailsPanel(detailsPanel, selected, student);
        });

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

    private void populateDetailsPanel(VBox panel, Course selected, User student) {
        panel.getChildren().clear();

        //check if required for user's major
        boolean requiredForMajor = curriculumService.isRequiredForMajor(student, selected);
        String requiredStr = requiredForMajor
            ? "Yes (required for your major)"
            : "No (elective for your major)";

        //recommended year for the course
        int recommendedYear = curriculumService.getRecommendedYear(student, selected);

        //eligibility for a course
        boolean eligible = curriculumService.isEligible(student, selected);
        String eligibleStr = eligible ? "✓ Yes" : "✗ No — prerequisites not met";
        String eligibleColor = eligible ? "#2E7D32" : "#C62828";
        Label eligibleLabel = new Label("Eligible to take: " + eligibleStr);
        eligibleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + eligibleColor + ";");

        //term availability
        List<String> terms = new java.util.ArrayList<>();
        if (selected.isOfferedFall())   terms.add("Fall");
        if (selected.isOfferedWinter()) terms.add("Winter");
        if (selected.isOfferedSummer()) terms.add("Summer");
        String termsStr = terms.isEmpty() ? "Not specified" : String.join(", ", terms);

        //prereqs from DB
        List<Prerequisite> prereqs = prerequisiteRepository.findByCourse(selected);
        String prereqStr;
        if (prereqs.isEmpty()) {
            prereqStr = "None";
        } else {
            prereqStr = prereqs.stream()
                .map(p -> p.getRequiredCourse().getCode() + " (" + p.getType() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");
        }

        //added course description information
        String description = (selected.getDescription() != null && !selected.getDescription().isBlank())
            ? selected.getDescription()
            : "No description available.";
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setStyle(UITheme.STYLE_BODY_LABEL);

        panel.getChildren().addAll(
            goldBar(),
            boldLabel(selected.getCode() + "  —  " + selected.getName()),
            bodyLabel("Credits: " + selected.getCredits()),
            bodyLabel("Year Level: " + selected.getYearLevel()),
            bodyLabel("Required: " + (selected.isRequired() ? "Yes" : "No")),
            bodyLabel("Category: " + (selected.getCategory() != null
                    ? selected.getCategory().toString() : "N/A")),
            bodyLabel("Offered: " + termsStr),
            bodyLabel("Prerequisites: " + prereqStr),
            sectionLabel("Description"),
            descLabel,
            new Separator(),
            eligibleLabel
        );
    }

    //Helpers ----------------------------------
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
}
    //-----------------------------------------------------------------------------------------------