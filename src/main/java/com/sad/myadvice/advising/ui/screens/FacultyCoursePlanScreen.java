package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.service.CoursePlanService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.CoursePlanItem;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.List;
//Faculty can search for a student to see their course plans and review them
//Can add notes and approve plans
@Component
public class FacultyCoursePlanScreen {
    //services/repository
    private final UserRepository userRepository;
    private final CoursePlanService coursePlanService;
    public FacultyCoursePlanScreen(UserRepository userRepository,
                                   CoursePlanService coursePlanService) {
        this.userRepository = userRepository;
        this.coursePlanService = coursePlanService;
    }

    //main pane
    public VBox build(User faculty) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Annotate Course Plans"));

        //student lookup card ---
        TextField searchField = styledTextField("Enter student name or student ID...");
        searchField.setPrefWidth(320);
        Button searchBtn = primaryButton("Search");
        Button clearBtn = secondaryButton("Clear");
        Label searchStatus = bodyLabel("");
        //adding search bar
        HBox searchBar = new HBox(10, searchField, searchBtn, clearBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        //list view of all students
        ListView<String> studentList = styledListView(110);
        final List<User>[] foundStudents = new List[]{List.of()};

        //plans list for selected student
        Label plansHeader = sectionLabel("Student's Plans");
        ListView<String> plansList = styledListView(110);
        final List<CoursePlan>[] foundPlans = new List[]{List.of()};

        //plan detail + annotation panel
        VBox planDetailPanel = new VBox(10);
        planDetailPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        planDetailPanel.getChildren().add(bodyLabel("Select a student, then select a plan to review."));

        //search students action for button
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            //if empty update status
            if (query.isEmpty()) { searchStatus.setText("Please enter a name or student ID."); return; }
            searchStatus.setText(""); //clearing everything
            studentList.getItems().clear();
            plansList.getItems().clear();
            foundPlans[0] = List.of();
            planDetailPanel.getChildren().setAll(bodyLabel("Select a student, then select a plan to review."));

            //filtering by students whose name or student ID matches
            List<User> students = userRepository.findByRole(User.Role.STUDENT).stream()
                .filter(u -> u.getName().toLowerCase().contains(query.toLowerCase())
                          || (u.getStudentId() != null && u.getStudentId().toLowerCase().contains(query.toLowerCase())))
                .toList();
            
            //different actions if found or not found
            foundStudents[0] = students;
            if (students.isEmpty()) {
                studentList.getItems().add("No students found.");
            } else {
                for (User s : students) { //adding to list if found
                    studentList.getItems().add(
                        s.getName() + "  |  ID: " + s.getStudentId()
                        + "  |  Major: " + (s.getMajor() != null ? s.getMajor() : "N/A")
                    );
                }
            }
        });
        //allowing enter key press to search
        searchField.setOnAction(e -> searchBtn.fire());

        //clear button action, clears everything and resets prompt
        clearBtn.setOnAction(e -> {
            searchField.clear();
            searchStatus.setText("");
            studentList.getItems().clear();
            plansList.getItems().clear();
            foundStudents[0] = List.of();
            foundPlans[0] = List.of();
            planDetailPanel.getChildren().setAll(bodyLabel("Select a student, then select a plan to review."));
        });

        //select student -> load their plans
        studentList.setOnMouseClicked(e -> {
            int idx = studentList.getSelectionModel().getSelectedIndex(); //click 
            if (idx < 0 || idx >= foundStudents[0].size()) return;
            User student = foundStudents[0].get(idx);
            //getting all plans
            plansList.getItems().clear();
            planDetailPanel.getChildren().setAll(bodyLabel("Select a plan to review."));
            List<CoursePlan> plans = coursePlanService.getPlansForStudent(student);
            foundPlans[0] = plans;
            //if empty, tell faculty, otherwise show plan status and details
            if (plans.isEmpty()) {
                plansList.getItems().add("This student has no course plans.");
            } else {
                for (CoursePlan p : plans) {
                    String status = p.isApproved()
                        ? "✓ Approved by " + p.getApprovedBy().getName()
                        : "— Pending review";
                    plansList.getItems().add(p.getPlanName() + "   |  " + status
                        + "   |  Created: " + p.getCreatedAt().toLocalDate());
                }
            }
        });

        //select plan -> show detail panel
        plansList.setOnMouseClicked(e -> {
            int idx = plansList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= foundPlans[0].size()) return;
            CoursePlan plan = foundPlans[0].get(idx);
            int studentIdx = studentList.getSelectionModel().getSelectedIndex();
            User student = foundStudents[0].get(studentIdx);
            //adding details 
            populatePlanPanel(planDetailPanel, plan, student, faculty, plansList, foundPlans);
        });

        //adding comopnents to the search card
        VBox searchCard = new VBox(10,
            goldBar(),
            sectionLabel("Student Lookup"),
            searchBar, searchStatus, studentList,
            plansHeader, plansList
        );//styling
        searchCard.setStyle(UITheme.STYLE_CARD);
        searchCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //adding components to the detail card
        VBox detailCard = new VBox(10,
            goldBar(),
            sectionLabel("Plan Review"),
            planDetailPanel
        );//styling
        detailCard.setStyle(UITheme.STYLE_CARD);
        detailCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //adding all cards to the main view
        view.getChildren().addAll(searchCard, detailCard);
        return view;
    }

    //function to populate the plan panel with plan info
    private void populatePlanPanel(VBox panel, CoursePlan plan, User student, User faculty, ListView<String> plansList, List<CoursePlan>[] foundPlans) {
        panel.getChildren().clear();
        //plan header info
        String approvalStatus = plan.isApproved()
            ? "✓ Approved by " + plan.getApprovedBy().getName()
            : "— Pending review";

        panel.getChildren().addAll(
            boldLabel("Plan: " + plan.getPlanName()),
            bodyLabel("Student: " + student.getName() + "  |  ID: " + student.getStudentId()),
            bodyLabel("Created: " + plan.getCreatedAt().toLocalDate()
                + "   |  Status: " + approvalStatus)
        );

        //the course items in this plan
        Label coursesHeader = sectionLabel("Planned Courses");
        ListView<String> coursesList = styledListView(160);
        List<CoursePlanItem> items = plan.getItems();
        //if no courses added / if added then show 
        if (items == null || items.isEmpty()) {
            coursesList.getItems().add("No courses added to this plan yet.");
        } else {
            for (CoursePlanItem item : items) {
                coursesList.getItems().add(
                    item.getCourse().getCode() + "  —  " + item.getCourse().getName()
                    + "   |  Term: " + item.getPlannedTerm()
                    + "   |  Year " + item.getPlannedYear()
                    + "   |  Credits: " + item.getCourse().getCredits()
                );
            }
        }
        //validating the validity of the plan based on prereqs 
        boolean isValid = coursePlanService.validatePlan(plan);
        Label validationLabel = new Label(isValid
            ? "✓ All prerequisites are satisfied for this plan."
            : "✗ Warning: Some courses in this plan have unmet prerequisites.");
        validationLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: "
            + (isValid ? "#2E7D32" : "#C62828") + ";");

        //faculty notes section
        Label notesHeader = sectionLabel("Faculty Notes / Feedback");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter feedback or notes for this student about their plan...");
        notesArea.setStyle(UITheme.STYLE_TEXT_FIELD);
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        //pre-populate as context
        if (student.getAdvisingNotes() != null && !student.getAdvisingNotes().isEmpty()) {
            notesArea.setText(student.getAdvisingNotes());
        }

        Label actionStatus = bodyLabel("");

        //approve button
        Button approveBtn = primaryButton(plan.isApproved() ? "✓ Already Approved" : "Approve Plan");
        approveBtn.setDisable(plan.isApproved());
        //approve action
        approveBtn.setOnAction(e -> {
            //save notes to student profile first
            String notes = notesArea.getText().trim();
            if (!notes.isEmpty()) {
                student.setAdvisingNotes(notes);
                userRepository.save(student);
            }
            //then approve the plan
            coursePlanService.approvePlan(plan, faculty);
            approveBtn.setDisable(true);
            approveBtn.setText("✓ Already Approved");
            actionStatus.setText("✓ Plan approved and notes saved.");
            actionStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            //refresh plans list
            refreshPlansList(plansList, foundPlans);
        });

        //save notes only (without approving)
        Button saveNotesBtn = secondaryButton("Save Notes Only");
        saveNotesBtn.setOnAction(e -> { //on click
            String notes = notesArea.getText().trim();
            student.setAdvisingNotes(notes);
            userRepository.save(student);
            actionStatus.setText("✓ Notes saved.");
            actionStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
        });

        //adding to a bar
        HBox actionBar = new HBox(10, approveBtn, saveNotesBtn);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        //adding all components to panel
        panel.getChildren().addAll(
            new Separator(),
            coursesHeader, coursesList,
            validationLabel,
            new Separator(),
            notesHeader, notesArea,
            actionBar, actionStatus
        );
    }

    //refreshing the list
    private void refreshPlansList(ListView<String> plansList, List<CoursePlan>[] foundPlans) {
        plansList.getItems().clear();
        for (CoursePlan p : foundPlans[0]) {
            String status = p.isApproved()
                ? "✓ Approved by " + p.getApprovedBy().getName()
                : "— Pending review";
            plansList.getItems().add(p.getPlanName() + "   |  " + status
                + "   |  Created: " + p.getCreatedAt().toLocalDate());
        }
    }

    //Helpers --------------------------------------------------------------------------------
    private Region goldBar() { Region bar = new Region(); bar.setStyle(UITheme.STYLE_GOLD_BAR); bar.setMaxWidth(Double.MAX_VALUE); return bar; }
    private Label pageTitle(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Label boldLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_BODY_LABEL + " -fx-font-weight: bold;"); return l; }
    private Button primaryButton(String text) { Button b = new Button(text); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String text) { Button b = new Button(text); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private TextField styledTextField(String prompt) { TextField tf = new TextField(); tf.setPromptText(prompt); tf.setStyle(UITheme.STYLE_TEXT_FIELD); tf.setMaxWidth(Double.MAX_VALUE); return tf; }
    private ListView<String> styledListView(double height) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(height); return lv; }
}