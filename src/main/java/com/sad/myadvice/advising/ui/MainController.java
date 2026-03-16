package com.sad.myadvice.advising.ui;

import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.service.CoursePlanService;
import com.sad.myadvice.advising.service.TranscriptService;
import com.sad.myadvice.advising.service.CourseService;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {

    @FXML private StackPane contentArea;

    private final CurriculumService curriculumService;
    private final TranscriptService transcriptService;
    private final CoursePlanService coursePlanService;
    private final CourseService courseService;
    private final UserRepository userRepository;

    // Hardcoded test student
    private User currentStudent;

    public MainController(CurriculumService curriculumService,
                          TranscriptService transcriptService,
                          CoursePlanService coursePlanService,
                          CourseService courseService,
                          UserRepository userRepository) {
        this.curriculumService = curriculumService;
        this.transcriptService = transcriptService;
        this.coursePlanService = coursePlanService;
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load test student from DB
        currentStudent = userRepository.findByStudentId("110177359");
        // Show progress screen by default
        showProgress();
    }

    // ── MY PROGRESS ──────────────────────────────────────────────────────────
    @FXML
    public void showProgress() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));

        Label title = new Label("My Degree Progress");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Completion percentage
        List<Course> required = courseService.getRequiredCourses();
        double pct = transcriptService.getCompletionPercentage(currentStudent, required.size());
        Label completion = new Label(String.format("Completion: %.1f%%", pct));
        completion.setStyle("-fx-font-size: 14;");

        ProgressBar progressBar = new ProgressBar(pct / 100);
        progressBar.setPrefWidth(400);

        // Remaining required courses
        Label remainingLabel = new Label("Remaining Required Courses:");
        remainingLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        ListView<String> remainingList = new ListView<>();
        List<Course> remaining = curriculumService.getRemainingRequiredCourses(currentStudent);
        for (Course c : remaining) {
            remainingList.getItems().add(c.getCode() + " — " + c.getName());
        }
        remainingList.setPrefHeight(200);

        view.getChildren().addAll(title, completion, progressBar, remainingLabel, remainingList);
        setContent(view);
    }

    // ── ELIGIBLE COURSES ─────────────────────────────────────────────────────
    @FXML
    public void showEligible() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));

        Label title = new Label("Courses You Can Take Now");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label reqLabel = new Label("Required:");
        reqLabel.setStyle("-fx-font-weight: bold;");
        ListView<String> reqList = new ListView<>();
        for (Course c : curriculumService.getEligibleRequired(currentStudent)) {
            reqList.getItems().add(c.getCode() + " — " + c.getName() + " (Yr " + c.getYearLevel() + ")");
        }
        reqList.setPrefHeight(180);

        Label elecLabel = new Label("Electives:");
        elecLabel.setStyle("-fx-font-weight: bold;");
        ListView<String> elecList = new ListView<>();
        for (Course c : curriculumService.getEligibleElectives(currentStudent)) {
            elecList.getItems().add(c.getCode() + " — " + c.getName() + " (Yr " + c.getYearLevel() + ")");
        }
        elecList.setPrefHeight(180);

        view.getChildren().addAll(title, reqLabel, reqList, elecLabel, elecList);
        setContent(view);
    }

    // ── COURSE DETAILS ───────────────────────────────────────────────────────
    @FXML
    public void showCourseDetails() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));

        Label title = new Label("Course Details");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Search bar
        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by code or name...");
        searchField.setPrefWidth(300);
        Button searchBtn = new Button("Search");
        searchBar.getChildren().addAll(searchField, searchBtn);

        // Results list
        ListView<String> resultsList = new ListView<>();
        resultsList.setPrefHeight(150);

        // Details panel
        VBox detailsPanel = new VBox(5);
        detailsPanel.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10;");
        Label detailsPlaceholder = new Label("Select a course to see details");
        detailsPanel.getChildren().add(detailsPlaceholder);

        // Search action
        searchBtn.setOnAction(e -> {
            resultsList.getItems().clear();
            List<Course> results = courseService.searchCourses(searchField.getText());
            if (results.isEmpty()) {
                resultsList.getItems().add("No courses found");
            } else {
                for (Course c : results) {
                    resultsList.getItems().add(c.getCode() + " — " + c.getName());
                }
            }
        });

        // Click a result to see details
        resultsList.setOnMouseClicked(e -> {
            int idx = resultsList.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            List<Course> results = courseService.searchCourses(searchField.getText());
            Course selected = results.get(idx);

            detailsPanel.getChildren().clear();
            detailsPanel.getChildren().addAll(
                bold(selected.getCode() + " — " + selected.getName()),
                new Label("Credits: " + selected.getCredits()),
                new Label("Year Level: " + selected.getYearLevel()),
                new Label("Required: " + (selected.isRequired() ? "Yes" : "No")),
                new Label("Category: " + selected.getCategory()),
                new Label("Eligible to take: " +
                    (curriculumService.isEligible(currentStudent, selected) ? "✓ Yes" : "✗ No"))
            );
        });

        view.getChildren().addAll(title, searchBar, resultsList, detailsPanel);
        setContent(view);
    }

    // ── MY PLANS ─────────────────────────────────────────────────────────────
    @FXML
    public void showPlans() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));

        Label title = new Label("My Course Plans");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Create new plan
        HBox newPlanBar = new HBox(10);
        TextField planNameField = new TextField();
        planNameField.setPromptText("New plan name...");
        Button createBtn = new Button("Create Plan");
        newPlanBar.getChildren().addAll(planNameField, createBtn);

        // Plans list
        ListView<String> plansList = new ListView<>();
        plansList.setPrefHeight(200);

        Label statusLabel = new Label("");

        // Load existing plans
        Runnable refreshPlans = () -> {
            plansList.getItems().clear();
            for (CoursePlan p : coursePlanService.getPlansForStudent(currentStudent)) {
                String approved = p.isApproved() ? " ✓ Approved" : " — Pending";
                plansList.getItems().add(p.getPlanName() + approved);
            }
        };
        refreshPlans.run();

        // Create plan action
        createBtn.setOnAction(e -> {
            String name = planNameField.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("Please enter a plan name.");
                return;
            }
            coursePlanService.createPlan(currentStudent, name);
            planNameField.clear();
            statusLabel.setText("Plan created!");
            refreshPlans.run();
        });

        view.getChildren().addAll(title, newPlanBar, plansList, statusLabel);
        setContent(view);
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private void setContent(VBox view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private Label bold(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        return l;
    }
}