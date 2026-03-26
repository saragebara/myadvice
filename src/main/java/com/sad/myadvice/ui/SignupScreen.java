package com.sad.myadvice.ui;

import com.sad.myadvice.entity.Major;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class SignupScreen {

    private final UserRepository userRepository;
    private java.util.function.Consumer<User> onSignupSuccess;
    private Runnable onBackToLogin;

    public SignupScreen(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setOnSignupSuccess(java.util.function.Consumer<User> callback) {
        this.onSignupSuccess = callback;
    }

    public void setOnBackToLogin(Runnable callback) {
        this.onBackToLogin = callback;
    }

    public VBox build() {
        VBox view = new VBox(16);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #F0F2F5;");
        view.setPadding(new Insets(40));

        //title
        Label title = new Label("myAdvice");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #003366;");
        //subtitle: Create an account
        Label subtitle = new Label("Create an Account");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #555555;");

        //gold divisor
        Region divider = new Region();
        divider.setStyle("-fx-background-color: #FFCC00; -fx-min-height: 3; -fx-max-height: 3;");
        divider.setPrefWidth(340);

        //signup card
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #003366; -fx-border-width: 2; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; " +
            "-fx-padding: 28;"
        );
        card.setMaxWidth(420);
        card.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("Sign Up");
        cardTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #003366;");

        //text field for name
        TextField nameField = styledField("Full Name", "FirstName LastName");
        //textfield for email
        TextField emailField = styledField("Email", "your@uwindsor.ca");
        //password
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");
        passwordField.setStyle("-fx-font-size: 13; -fx-padding: 8; " +
            "-fx-border-color: #D2D2D2; -fx-border-radius: 4; -fx-background-radius: 4;");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        //student/faculty ID prompt
        TextField idField = styledField("ID", "e.g. 110177359 (students) or faculty ID");

        //dropdown for selecting role (student, faculty or staff)
        Label roleLabel = new Label("Role");
        roleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("STUDENT", "FACULTY", "STAFF");
        roleCombo.setPromptText("Select your role");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle("-fx-font-size: 13;");

        // Error / success label
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12;");
        statusLabel.setWrapText(true);

        // Major dropdown (only shown for students)
        Label majorLabel = new Label("Major");
        majorLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        majorLabel.setVisible(false);
        majorLabel.setManaged(false);

        ComboBox<String> majorCombo = new ComboBox<>();
        majorCombo.getItems().addAll(
            "Bachelor of Computer Science (General)",
            "Bachelor of Computer Science (Honours)",
            "Bachelor of Computer Science (Honours Applied Computing)",
            "Bachelor of Science (Honours Computer Information Systems)",
            "Bachelor of Science (Honours CS with Software Engineering)",
            "Bachelor of Commerce (Honours Business Administration and CS)",
            "Bachelor of Mathematics (Honours Mathematics and CS)",
            "Bachelor of Information Technology"
        );
        majorCombo.setPromptText("Select your major");
        majorCombo.setMaxWidth(Double.MAX_VALUE);
        majorCombo.setStyle("-fx-font-size: 13;");
        majorCombo.setVisible(false);
        majorCombo.setManaged(false);

        //make major info visible if user is a student
        roleCombo.setOnAction(e -> {
            boolean isStudent = "STUDENT".equals(roleCombo.getValue());
            majorLabel.setVisible(isStudent);
            majorLabel.setManaged(isStudent);
            majorCombo.setVisible(isStudent);
            majorCombo.setManaged(isStudent);
        });

        //sign up button
        Button signupBtn = new Button("Create Account");
        signupBtn.setStyle(
            "-fx-background-color: #003366; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-font-weight: bold; " +
            "-fx-background-radius: 6; -fx-padding: 10 20 10 20; -fx-cursor: hand;"
        );
        signupBtn.setMaxWidth(Double.MAX_VALUE);

        //back button to log in
        Button backBtn = new Button("Already have an account? Sign In");
        backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #003366; " +
            "-fx-font-size: 12; -fx-cursor: hand; -fx-underline: true;"
        );

        //Signup logic ---------------------------------------------------------
        signupBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String id = idField.getText().trim();
            String roleStr = roleCombo.getValue();

            //error validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()
                    || id.isEmpty() || roleStr == null) {
                statusLabel.setText("⚠ Please fill in all fields.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12;");
                return;
            }

            //check if email already exists
            if (userRepository.findByEmail(email) != null) {
                statusLabel.setText("⚠ An account with that email already exists.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12;");
                return;
            }

            //check if student ID already exists
            if (userRepository.findByStudentId(id) != null) {
                statusLabel.setText("⚠ An account with that ID already exists.");
                statusLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12;");
                return;
            }

            //all checks passed, create new user and save to DB
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setStudentId(id);
            newUser.setRole(User.Role.valueOf(roleStr));
            userRepository.save(newUser);
            if ("STUDENT".equals(roleStr) && majorCombo.getValue() != null) { //set major if role is student
                Major major = Major.fromFullName(majorCombo.getValue());
                newUser.setMajor(major);
}

            statusLabel.setText("✓ Account created successfully!");
            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 12;");

            if (onSignupSuccess != null) onSignupSuccess.accept(newUser);
        });

        //back to log-in action
        backBtn.setOnAction(e -> {
            if (onBackToLogin != null) onBackToLogin.run();
        });

        card.getChildren().addAll(
            cardTitle,
            buildFieldGroup("Full Name", nameField),
            buildFieldGroup("Email", emailField),
            passwordLabel, passwordField,
            buildFieldGroup("ID", idField),
            roleLabel, roleCombo,
            majorLabel, majorCombo,
            statusLabel, signupBtn, backBtn
        );

        view.getChildren().addAll(title, subtitle, divider, card);
        return view;
    }

    //helpers ---------------------------------------------------------

    private TextField styledField(String label, String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-font-size: 13; -fx-padding: 8; " +
            "-fx-border-color: #D2D2D2; -fx-border-radius: 4; -fx-background-radius: 4;");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private VBox buildFieldGroup(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        VBox group = new VBox(4, label, field);
        return group;
    }
}