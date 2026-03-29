package com.sad.myadvice.administering.ui.screens;

import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.Transcript;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.TranscriptRepository;
import com.sad.myadvice.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentProfileScreen {

    private final UserRepository userRepository;
    private final TranscriptRepository transcriptRepository;

    public StudentProfileScreen(UserRepository userRepository,
                                 TranscriptRepository transcriptRepository) {
        this.userRepository = userRepository;
        this.transcriptRepository = transcriptRepository;
    }

    public VBox build(User student) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.getChildren().add(pageTitle("My Profile"));

        // ── Profile card ──────────────────────────────────────────────────────
        TextField nameField = styledField(student.getName());
        TextField emailField = styledField(student.getEmail());
        emailField.setEditable(false);
        emailField.setStyle(UITheme.STYLE_TEXT_FIELD +
            " -fx-background-color: #F0F0F0;");

        TextField studentIdField = styledField(
            student.getStudentId() != null ? student.getStudentId() : "N/A");
        studentIdField.setEditable(false);
        studentIdField.setStyle(UITheme.STYLE_TEXT_FIELD +
            " -fx-background-color: #F0F0F0;");

        Label majorLabel = bodyLabel(
            student.getMajor() != null
                ? student.getMajor().getFullName()
                : "No major selected"
        );

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password (leave blank to keep current)");
        newPasswordField.setStyle(UITheme.STYLE_TEXT_FIELD);
        newPasswordField.setMaxWidth(Double.MAX_VALUE);

        Label profileStatus = new Label("");
        profileStatus.setStyle(UITheme.STYLE_BODY_LABEL);

        Button saveProfileBtn = primaryButton("Save Changes");
        saveProfileBtn.setOnAction(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                profileStatus.setText("⚠ Name cannot be empty.");
                profileStatus.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13;");
                return;
            }
            student.setName(newName);
            if (!newPasswordField.getText().isEmpty()) {
                student.setPassword(newPasswordField.getText());
            }
            userRepository.save(student);
            profileStatus.setText("✓ Profile updated successfully!");
            profileStatus.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13;");
            newPasswordField.clear();
        });

        VBox profileCard = new VBox(10,
            goldBar(),
            sectionLabel("Personal Information"),
            buildRow("Full Name",   nameField),
            buildRow("Email",       emailField),
            buildRow("Student ID",  studentIdField),
            buildFieldGroup("Major", majorLabel),
            buildFieldGroup("New Password", newPasswordField),
            saveProfileBtn,
            profileStatus
        );
        profileCard.setStyle(UITheme.STYLE_CARD);
        profileCard.setPadding(new Insets(UITheme.CARD_PADDING));

        // ── Transcript card ───────────────────────────────────────────────────
        List<Transcript> transcripts = transcriptRepository.findByStudent(student);

        TableView<Transcript> transcriptTable = new TableView<>();
        transcriptTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        transcriptTable.setPrefHeight(280);

        TableColumn<Transcript, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getCourse().getCode()));

        TableColumn<Transcript, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getCourse().getName()));

        TableColumn<Transcript, String> termCol = new TableColumn<>("Term");
        termCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getTerm()));

        TableColumn<Transcript, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(cd -> {
            Double g = cd.getValue().getGrade();
            return new javafx.beans.property.SimpleStringProperty(
                g != null ? g + "%" : "—");
        });

        TableColumn<Transcript, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getStatus().toString()));

        transcriptTable.getColumns().addAll(
            codeCol, nameCol, termCol, gradeCol, statusCol);
        transcriptTable.getItems().addAll(transcripts);

        // Summary stats
        long completed = transcripts.stream()
            .filter(t -> t.getStatus() == Transcript.Status.COMPLETED).count();
        long inProgress = transcripts.stream()
            .filter(t -> t.getStatus() == Transcript.Status.IN_PROGRESS).count();
        double avgGrade = transcripts.stream()
            .filter(t -> t.getGrade() != null)
            .mapToDouble(Transcript::getGrade)
            .average()
            .orElse(0.0);

        HBox stats = new HBox(20,
            statBox("Completed",   String.valueOf(completed)),
            statBox("In Progress", String.valueOf(inProgress)),
            statBox("Average Grade", String.format("%.1f%%", avgGrade))
        );
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox transcriptCard = new VBox(10,
            goldBar(),
            sectionLabel("My Transcript"),
            stats,
            transcriptTable
        );
        transcriptCard.setStyle(UITheme.STYLE_CARD);
        transcriptCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(profileCard, transcriptCard);
        return view;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VBox statBox(String label, String value) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: "
            + UITheme.UW_BLUE + ";");
        Label labelLabel = new Label(label);
        labelLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        VBox box = new VBox(2, valueLabel, labelLabel);
        box.setStyle(
            "-fx-background-color: white; -fx-border-color: " + UITheme.UW_BLUE
            + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10 20 10 20;");
        return box;
    }

    private HBox buildRow(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle(UITheme.STYLE_BODY_LABEL);
        label.setMinWidth(100);
        field.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(field, Priority.ALWAYS);
        HBox row = new HBox(10, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildFieldGroup(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle(UITheme.STYLE_BODY_LABEL);
        return new VBox(4, label, field);
    }

    private Region goldBar() {
        Region bar = new Region();
        bar.setStyle(UITheme.STYLE_GOLD_BAR);
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }

    private Label pageTitle(String t) {
        Label l = new Label(t); l.setStyle(UITheme.STYLE_PAGE_TITLE); return l;
    }

    private Label sectionLabel(String t) {
        Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l;
    }

    private Label bodyLabel(String t) {
        Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l;
    }

    private Button primaryButton(String t) {
        Button b = new Button(t); b.setStyle(UITheme.STYLE_PRIMARY_BUTTON); return b;
    }

    private TextField styledField(String value) {
        TextField tf = new TextField(value);
        tf.setStyle(UITheme.STYLE_TEXT_FIELD);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }
}