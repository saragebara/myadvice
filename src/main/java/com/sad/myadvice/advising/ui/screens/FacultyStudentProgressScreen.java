package com.sad.myadvice.advising.ui.screens;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sad.myadvice.advising.service.CurriculumService;
import com.sad.myadvice.advising.service.TranscriptService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Transcript;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

//FACULTY ADVISING - search students by name/ID and see transcript, degree completion, progress, remaining req courses
//can also write notes to the student's profile for advising
@Component
public class FacultyStudentProgressScreen {
    //services and repository
    private final UserRepository userRepository;
    private final CurriculumService curriculumService;
    private final TranscriptService transcriptService;
    public FacultyStudentProgressScreen(UserRepository userRepository,
                                        CurriculumService curriculumService,
                                        TranscriptService transcriptService) {
        this.userRepository = userRepository;
        this.curriculumService = curriculumService;
        this.transcriptService = transcriptService;
    }

    //main content pane for student progress
    public VBox build(User faculty) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("Student Academic Progress"));

        //search for student
        TextField searchField = styledTextField("Enter student name or student ID...");
        searchField.setPrefWidth(320);
        Button searchBtn = primaryButton("Search");
        Button clearBtn = secondaryButton("Clear");
        Label searchStatus = bodyLabel("");
        //adding search bar
        HBox searchBar = new HBox(10, searchField, searchBtn, clearBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        //list of students found from search
        ListView<String> studentList = styledListView(120);
        final List<User>[] foundStudents = new List[]{List.of()};

        //progress panel -- shown after select a user
        VBox progressPanel = new VBox(10);
        progressPanel.setStyle(UITheme.STYLE_DETAILS_PANEL);
        //by default it says to search for a student first
        progressPanel.getChildren().add(bodyLabel("Search for a student to view their progress."));

        //search button action
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) { //if empty 
                searchStatus.setText("Please enter a name or student ID.");
                return;
            }
            searchStatus.setText("");
            studentList.getItems().clear(); //clearing the list before adding results
            progressPanel.getChildren().setAll(bodyLabel("Select a student from the results."));
            //finding students 
            List<User> students = userRepository.findByRole(User.Role.STUDENT).stream()
                .filter(u -> u.getName().toLowerCase().contains(query.toLowerCase()) //filter by name
                            //or by student ID
                          || (u.getStudentId() != null && u.getStudentId().toLowerCase().contains(query.toLowerCase())))
                .toList();
            foundStudents[0] = students;
            if (students.isEmpty()) { //no students
                studentList.getItems().add("No students found.");
            } else { //otherwise add them to list view
                for (User s : students) {
                    studentList.getItems().add(
                        s.getName() + "  |  ID: " + s.getStudentId()
                        + "  |  Major: " + (s.getMajor() != null ? s.getMajor() : "N/A") //if major is somehow null
                    );
                }
            }
        });
        searchField.setOnAction(e -> searchBtn.fire()); //press enter to search
        
        //clear button action, clears all the fields and the list
        clearBtn.setOnAction(e -> {
            searchField.clear();
            searchStatus.setText("");
            studentList.getItems().clear();
            foundStudents[0] = List.of();
            progressPanel.getChildren().setAll(bodyLabel("Search for a student to view their progress."));
        });

        //select student -> show progress
        studentList.setOnMouseClicked(e -> {
            int idx = studentList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= foundStudents[0].size()) return;
            User student = foundStudents[0].get(idx);
            populateProgressPanel(progressPanel, student, faculty); //showing progress panel
        });

        //adding components to search card
        VBox searchCard = new VBox(10,
            goldBar(),
            sectionLabel("Student Lookup"),
            searchBar,
            searchStatus,
            studentList
        );//styling
        searchCard.setStyle(UITheme.STYLE_CARD);
        searchCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //progress card wrapper ---------------------------------------
        VBox progressCard = new VBox(10, goldBar(), sectionLabel("Academic Progress"), progressPanel);
        progressCard.setStyle(UITheme.STYLE_CARD);
        progressCard.setPadding(new Insets(UITheme.CARD_PADDING));

        //adding cards to main view
        view.getChildren().addAll(searchCard, progressCard);
        return view;
    }

    //progress panel logic
    private void populateProgressPanel(VBox panel, User student, User faculty) {
        panel.getChildren().clear();
        //basic info - 
        List<Course> required = curriculumService.getRequiredCoursesForMajor(student);
        List<Course> completed = transcriptService.getCompletedCourses(student);
        List<Course> inProgress = transcriptService.getInProgressCourses(student);
        List<Course> remaining = curriculumService.getRemainingRequiredCourses(student);
        double pct = transcriptService.getCompletionPercentage(student, required.size());
        //student's name
        Label nameLabel = boldLabel(student.getName()
            + "  |  ID: " + student.getStudentId()
            + "  |  Major: " + (student.getMajor() != null ? student.getMajor() : "N/A"));

        //progress bar
        ProgressBar progressBar = new ProgressBar(pct / 100);
        progressBar.setPrefWidth(500);
        progressBar.setStyle(UITheme.STYLE_PROGRESS_BAR);
        Label pctLabel = new Label(String.format("%.1f%% of required courses completed", pct));
        pctLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + UITheme.UW_BLUE + ";");
        //stats row with completed and remaining courses
        Label statsLabel = bodyLabel(
            "Completed: " + completed.size()
            + "   |   In Progress: " + inProgress.size()
            + "   |   Remaining Required: " + remaining.size()
        );

        //transcript table
        Label transcriptHeader = sectionLabel("Full Transcript");
        ListView<String> transcriptList = styledListView(160);
        //getting the student's ful ltranscript to put into the list view
        List<Transcript> fullTranscript = transcriptService.getFullTranscript(student);
        if (fullTranscript.isEmpty()) { //if empty
            transcriptList.getItems().add("No transcript records found.");
        } else {
            for (Transcript t : fullTranscript) { //parsing through to get grades for each course and status
                String grade = t.getGrade() != null ? String.format("%.1f%%", t.getGrade()) : "—";
                transcriptList.getItems().add(
                    t.getCourse().getCode() + "  —  " + t.getCourse().getName()
                    + "   |  Term: " + t.getTerm()
                    + "   |  Grade: " + grade
                    + "   |  Status: " + t.getStatus()
                );
            }
        }

        //remaining required courses for a student
        Label remainingHeader = sectionLabel("Remaining Required Courses (" + remaining.size() + ")");
        ListView<String> remainingList = styledListView(120);
        if (remaining.isEmpty()) { //if all completed / empty
            remainingList.getItems().add("✓ All required courses completed!");
        } else {
            for (Course c : remaining) { //otherwise add each course
                remainingList.getItems().add(
                    c.getCode() + "  —  " + c.getName() + "  (Year " +c.getYearLevel() + ")"
                );
            }
        }

        //advising notes 
        Label notesHeader = sectionLabel("Advising Notes");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter advising notes for this student (saved to their profile)...");
        notesArea.setStyle(UITheme.STYLE_TEXT_FIELD);
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        //pre-populate existing notes if any
        if (student.getAdvisingNotes() != null && !student.getAdvisingNotes().isEmpty()) {
            notesArea.setText(student.getAdvisingNotes());
        }
        //note status
        Label noteStatus = bodyLabel("");
        //button to save notes and on action update DB
        Button saveNotesBtn = primaryButton("Save Notes");
        saveNotesBtn.setOnAction(e -> {
            student.setAdvisingNotes(notesArea.getText().trim());
            userRepository.save(student);
            noteStatus.setText("✓ Notes saved successfully.");
            noteStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
        });

        //adding all components to panel
        panel.getChildren().addAll(
            nameLabel,
            progressBar, pctLabel,
            statsLabel,
            new Separator(),
            transcriptHeader, transcriptList,
            remainingHeader, remainingList,
            new Separator(),
            notesHeader, notesArea, saveNotesBtn, noteStatus
        );
    }

    //Helpers --------------------------------------------------------------
    private Region goldBar() {
        Region bar = new Region();
        bar.setStyle(UITheme.STYLE_GOLD_BAR);
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }
    private Label pageTitle(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l; }
    private Label sectionLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
    private Label boldLabel(String text) { Label l = new Label(text); l.setStyle(UITheme.STYLE_BODY_LABEL + " -fx-font-weight: bold;"); return l; }
    private Button primaryButton(String text) { Button b = new Button(text); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b; }
    private Button secondaryButton(String text) { Button b = new Button(text); b.setStyle(UITheme.STYLE_SECONDARY_BUTTON); return b; }
    private TextField styledTextField(String prompt) { TextField tf = new TextField(); tf.setPromptText(prompt); tf.setStyle(UITheme.STYLE_TEXT_FIELD); tf.setMaxWidth(Double.MAX_VALUE); return tf; }
    private ListView<String> styledListView(double height) { ListView<String> lv = new ListView<>(); lv.setStyle(UITheme.STYLE_LIST_VIEW); lv.setPrefHeight(height); return lv; }
}