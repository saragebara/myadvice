package com.sad.myadvice.advising.ui.screens;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sad.myadvice.advising.service.CoursePlanService;
import com.sad.myadvice.advising.service.CourseService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.CoursePlanItem;
import com.sad.myadvice.entity.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@Component
public class PlansScreen {

    private final CoursePlanService coursePlanService;
    private final CourseService courseService;

    public PlansScreen(CoursePlanService coursePlanService, CourseService courseService) {
        this.coursePlanService = coursePlanService;
        this.courseService = courseService;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("My Course Plans"));

        //use plain List/array wrappers instead of raw-typed array of List
        List<CoursePlan> currentPlans = new ArrayList<>();
        CoursePlan[] selectedPlan = {null};
        List<Course> searchResults = new ArrayList<>();
        Course[] selectedCourse = {null};

        //plan list card
        TextField planNameField = styledTextField("Enter a name for your new plan...");
        planNameField.setPrefWidth(260);
        Button createBtn = primaryButton("Create Plan");
        Label createStatus = bodyLabel("");
        HBox createBar = new HBox(10, planNameField, createBtn);
        createBar.setAlignment(Pos.CENTER_LEFT);
        ListView<String> plansList = styledListView(180);

        //plan details card ---
        Label selectedPlanLabel = sectionLabel("Select a plan to manage its courses.");
        ListView<String> planItemsList = styledListView(160);
        Label planValidationLabel = bodyLabel("");
        planValidationLabel.setWrapText(true);
        Button removeCourseBtn = secondaryButton("Remove Selected Course");
        Button deletePlanBtn = secondaryButton("Delete Plan");
        removeCourseBtn.setDisable(true);
        deletePlanBtn.setDisable(true);

        //course search card ---
        TextField courseKeywordField = styledTextField("Search by code, name, or keyword...");
        courseKeywordField.setPrefWidth(220);
        //combo box with years available
        ComboBox<String> yearFilter = new ComboBox<>();
        yearFilter.getItems().addAll("Any Year", "1", "2", "3", "4");
        yearFilter.setValue("Any Year");
        yearFilter.setStyle(UITheme.STYLE_TEXT_FIELD);
        //combo box with required/elective course
        ComboBox<String> requiredFilter = new ComboBox<>();
        requiredFilter.getItems().addAll("All", "Required", "Elective");
        requiredFilter.setValue("All");
        requiredFilter.setStyle(UITheme.STYLE_TEXT_FIELD);
        //checks timetable
        ComboBox<String> availabilityFilter = new ComboBox<>();
        availabilityFilter.getItems().addAll("All", "In Timetable");
        availabilityFilter.setValue("All");
        availabilityFilter.setStyle(UITheme.STYLE_TEXT_FIELD);
        //term check
        ComboBox<String> termFilter = new ComboBox<>();
        termFilter.getItems().addAll("Any Term", "Fall", "Winter", "Summer");
        termFilter.setValue("Any Term");
        termFilter.setStyle(UITheme.STYLE_TEXT_FIELD);
        //search button
        Button searchBtn = primaryButton("Search");
        ListView<String> courseResultsList = styledListView(160);
        Label courseStatus = bodyLabel("");

        //term + year fields for adding course to plan
        TextField plannedTermField = styledTextField("Term (e.g. 2026F, 2027W)");
        plannedTermField.setPrefWidth(180);
        TextField plannedYearField = styledTextField("Study year (1-4)");
        plannedYearField.setPrefWidth(120);
        Button addCourseBtn = primaryButton("Add to Plan");
        addCourseBtn.setDisable(false);

        //Refresh helpers---------------------------

        //refreshes the plans list view from DB
        Runnable refreshPlansList = () -> {
            plansList.getItems().clear();
            currentPlans.clear();
            currentPlans.addAll(coursePlanService.getPlansForStudent(student));
            if (currentPlans.isEmpty()) {
                plansList.getItems().add("No plans yet — create one above.");
            } else {
                for (CoursePlan p : currentPlans) {
                    String status = p.isApproved() ? "  [Approved]" : "  [Pending]";
                    plansList.getItems().add(p.getPlanName() + status);
                }
            }
        };

        //refreshes the plan detail panel for the currently selected plan
        //always reloads from DB via getPlanById() so  that items can't be stale
        Runnable refreshPlanDetails = () -> {
            if (selectedPlan[0] == null) return;

            selectedPlan[0] = coursePlanService.getPlanById(selectedPlan[0].getId());
            CoursePlan plan = selectedPlan[0];

            selectedPlanLabel.setText("Plan: " + plan.getPlanName()
                + (plan.isApproved() ? "  [Approved]" : "  [Pending faculty approval]"));

            planItemsList.getItems().clear();
            List<CoursePlanItem> items = plan.getItems();
            if (items == null || items.isEmpty()) { //if no courses
                planItemsList.getItems().add("No courses added yet.");
                removeCourseBtn.setDisable(false);
            } else { //otherwise add them all to the list
                for (CoursePlanItem item : items) {
                    Course c = item.getCourse();
                    planItemsList.getItems().add(
                        c.getCode() + "  —  " + c.getName()
                        + "  |  Term: " + item.getPlannedTerm()
                        + "  |  Study Year: " + item.getPlannedYear()
                    );
                }
                removeCourseBtn.setDisable(false);
            }

            //validation, always run on the fresh plan
            List<String> messages = coursePlanService.getValidationMessages(plan);
            if (messages.isEmpty()) {
                planValidationLabel.setText("✓ Plan looks valid.");
                planValidationLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            } else {
                planValidationLabel.setText("Issues:\n• " + String.join("\n• ", messages));
                planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }

            deletePlanBtn.setDisable(false);
            addCourseBtn.setDisable(false);
        };

        //select plan from list
        plansList.setOnMouseClicked(e -> {
            int idx = plansList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= currentPlans.size()) return;
            selectedPlan[0] = coursePlanService.getPlanById(currentPlans.get(idx).getId());
            refreshPlanDetails.run();
        });

        //create plan action
        createBtn.setOnAction(e -> {
            String name = planNameField.getText().trim();
            if (name.isEmpty()) { //if no name
                createStatus.setText("Please enter a plan name.");
                createStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            CoursePlan newPlan = coursePlanService.createPlan(student, name);
            planNameField.clear();
            createStatus.setText("Plan \"" + name + "\" created.");
            createStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            refreshPlansList.run();
            //auto-selecting the new plan made for convenience 
            selectedPlan[0] = newPlan;
            plansList.getSelectionModel().select(currentPlans.size() - 1);
            refreshPlanDetails.run();
        });

        //delete plan action
        deletePlanBtn.setOnAction(e -> {
            if (selectedPlan[0] == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, //alert to warn user
                "Delete plan \"" + selectedPlan[0].getPlanName() + "\"? This cannot be undone.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) { //if ok then reset values
                    coursePlanService.deletePlan(selectedPlan[0]);
                    selectedPlan[0] = null;
                    selectedPlanLabel.setText("Select a plan to manage its courses.");
                    planItemsList.getItems().clear();
                    planValidationLabel.setText("");
                    removeCourseBtn.setDisable(true);
                    deletePlanBtn.setDisable(true);
                    addCourseBtn.setDisable(true);
                    refreshPlansList.run();
                }
            });
        });

        //remove course from plan
        removeCourseBtn.setOnAction(e -> {
            //error checks-----------------------------
            if (selectedPlan[0] == null) {
                planValidationLabel.setText("⚠ Select a plan first.");
                planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            int idx = planItemsList.getSelectionModel().getSelectedIndex();
            if (idx < 0) {
                planValidationLabel.setText("⚠ Select a course from the plan to remove.");
                planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            String selectedText = planItemsList.getItems().get(idx);
            if (selectedText.equals("No courses added yet.")) {
                planValidationLabel.setText("⚠ No courses to remove.");
                planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }

            //confirmation alert/warning
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remove this course from the plan?");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        CoursePlan freshPlan = coursePlanService.getPlanById(selectedPlan[0].getId());
                        List<CoursePlanItem> items = freshPlan.getItems();
                        
                        int serviceIdx = -1;
                        for (int i = 0; i < items.size(); i++) {
                            Course c = items.get(i).getCourse();
                            String display = c.getCode() + "  —  " + c.getName()
                                + "  |  Term: " + items.get(i).getPlannedTerm()
                                + "  |  Study Year: " + items.get(i).getPlannedYear();
                            if (display.equals(selectedText)) {
                                serviceIdx = i;
                                break;
                            }
                        }

                        if (serviceIdx < 0) {
                            planValidationLabel.setText("✗ Could not match selected course — try re-selecting.");
                            planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                            return;
                        }

                        selectedPlan[0] = coursePlanService.removeItemFromPlan(selectedPlan[0], serviceIdx);

                        //refresh the list/validation FIRST, then the status message so it isn't immediately overwritten by refreshPlanDetails
                        refreshPlanDetails.run();
                        //overwrite after refresh
                        planValidationLabel.setText("✓ Course removed.");
                        planValidationLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");

                    } catch (Exception ex) {
                        planValidationLabel.setText("✗ Error: " + ex.getMessage());
                        planValidationLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                    }
                }
            });
        });

        // Course search
        searchBtn.setOnAction(e -> {
            Integer year = "Any Year".equals(yearFilter.getValue())
                ? null : Integer.valueOf(yearFilter.getValue());

            Boolean required = switch (requiredFilter.getValue()) {
                case "Required" -> true;
                case "Elective" -> false;
                default -> null;
            };

            String term = "Any Term".equals(termFilter.getValue())
                ? null : termFilter.getValue().toLowerCase();

            List<Course> results = courseService.searchCourses(
                courseKeywordField.getText(), year, required, term);

            boolean timetableOnly = "In Timetable".equals(availabilityFilter.getValue());
            if (timetableOnly) {
                results = results.stream()
                    .filter(c -> coursePlanService.isCourseOfferedInTimetable(c))
                    .toList();
            }

            searchResults.clear();
            searchResults.addAll(results);
            courseResultsList.getItems().clear();
            selectedCourse[0] = null;

            if (searchResults.isEmpty()) {
                courseResultsList.getItems().add("No courses found.");
            } else {
                for (Course c : searchResults) {
                    // Show required/elective tag relative to student's major
                    boolean req = coursePlanService
                        .isCourseOfferedInTimetable(c); // just for display
                    courseResultsList.getItems().add(
                        c.getCode() + "  —  " + c.getName()
                        + "  (Year " + c.getYearLevel() + ")"
                        + "  " + c.getCredits() + " cr"
                    );
                }
            }
            courseStatus.setText("");
        });

        //allow enter to trigger search
        courseKeywordField.setOnAction(e -> searchBtn.fire());

        //select a course from the results
        courseResultsList.setOnMouseClicked(e -> {
            int idx = courseResultsList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= searchResults.size()) {
                selectedCourse[0] = null;
                return;
            }
            selectedCourse[0] = searchResults.get(idx);
            courseStatus.setText("Selected: " + selectedCourse[0].getCode()
                + "  —  " + selectedCourse[0].getName());
            courseStatus.setStyle("-fx-text-fill: " + UITheme.UW_BLUE + "; -fx-font-size: 13;");
        });

        //add course to plan
        addCourseBtn.setOnAction(e -> {
            System.out.println("Add button clicked. Plan: " + (selectedPlan[0] == null ? "NULL" : selectedPlan[0].getPlanName()));
            System.out.println("Course: " + (selectedCourse[0] == null ? "NULL" : selectedCourse[0].getCode()));
            if (selectedPlan[0] == null) {
                courseStatus.setText("Select a plan first.");
                courseStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            if (selectedCourse[0] == null) {
                courseStatus.setText("Select a course to add.");
                courseStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            String term = plannedTermField.getText().trim();
            if (term.isEmpty()) {
                courseStatus.setText("Enter the planned term (e.g. 2026F).");
                courseStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            int plannedYear;
            try {
                plannedYear = Integer.parseInt(plannedYearField.getText().trim());
                if (plannedYear < 1 || plannedYear > 4) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                courseStatus.setText("Enter a study year between 1 and 4.");
                courseStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }

            try {
                selectedPlan[0] = coursePlanService.addItemToPlan(
                selectedPlan[0], selectedCourse[0], term, plannedYear);
                plannedTermField.clear();
                plannedYearField.clear();
                courseStatus.setText("Added " + selectedCourse[0].getCode() + " to plan.");
                courseStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
                selectedCourse[0] = null;
                courseResultsList.getSelectionModel().clearSelection();
                refreshPlanDetails.run();
            } catch (Exception ex) {
                courseStatus.setText("Error: " + ex.getMessage());
                courseStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
            }
        });

        VBox plansCard = new VBox(10,
            goldBar(),
            sectionLabel("Create a New Plan"),
            createBar, createStatus,
            sectionLabel("My Saved Plans"),
            plansList,
            deletePlanBtn
        );
        plansCard.setStyle(UITheme.STYLE_CARD);
        plansCard.setPadding(new Insets(UITheme.CARD_PADDING));

        VBox planDetailsCard = new VBox(10,
            goldBar(),
            sectionLabel("Plan Details"),
            selectedPlanLabel,
            planItemsList,
            removeCourseBtn,
            planValidationLabel
        );
        planDetailsCard.setStyle(UITheme.STYLE_CARD);
        planDetailsCard.setPadding(new Insets(UITheme.CARD_PADDING));
        
        plansCard.setPrefWidth(400);
        plansCard.setMaxWidth(400);

        planDetailsCard.setPrefWidth(400);
        planDetailsCard.setMaxWidth(400);

        planValidationLabel.setMaxWidth(380);
        planValidationLabel.setMaxHeight(80); // cap height

        HBox topRow = new HBox(10, plansCard, planDetailsCard);
        topRow.setFillHeight(false);

        HBox filterRow = new HBox(8,
            courseKeywordField, yearFilter, requiredFilter, termFilter, availabilityFilter, searchBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        HBox addRow = new HBox(10, plannedTermField, plannedYearField, addCourseBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);

        VBox courseSearchCard = new VBox(10,
            goldBar(),
            sectionLabel("Search & Add Courses"),
            bodyLabel("Search for courses, select one, fill in term and study year, then click Add to Plan."),
            filterRow,
            courseResultsList,
            courseStatus,
            addRow
        );
        courseSearchCard.setStyle(UITheme.STYLE_CARD);
        courseSearchCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(topRow, courseSearchCard);
        refreshPlansList.run();
        return view;
    }

    //Helpers -----------------------
    private Region goldBar() { Region b = new Region(); b.setStyle(UITheme.STYLE_GOLD_BAR); b.setMaxWidth(Double.MAX_VALUE); return b; }
    private Label pageTitle(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Button primaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String t) { Button b = new Button(t); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private TextField styledTextField(String p) { TextField tf = new TextField(); tf.setPromptText(p); tf.setStyle(UITheme.STYLE_TEXT_FIELD); return tf; }
    private ListView<String> styledListView(double h) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(h); return lv; }
}