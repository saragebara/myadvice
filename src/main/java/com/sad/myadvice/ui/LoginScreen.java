package com.sad.myadvice.ui;

import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class LoginScreen {
    private final UserRepository userRepository;
    private java.util.function.Consumer<User> onLoginSuccess;
    private Runnable onSignUp;

    public LoginScreen(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //actions on login/on sign up
    public void setOnLoginSuccess(java.util.function.Consumer<User> callback) {
        this.onLoginSuccess = callback;
    }
    public void setOnSignUp(Runnable callback) {
        this.onSignUp = callback;
    }

    //main container
    public VBox build() {
        //styling
        VBox view = new VBox(16);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #F0F2F5;");
        view.setPadding(new Insets(60));

        //Logo/title ---------------------------
        Label title = new Label("myAdvice");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #003366;");

        Label subtitle = new Label("University of Windsor — CS Advising System");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #555555;");

        //gold divider ---------------------------
        Region divider = new Region();
        divider.setStyle("-fx-background-color: #FFCC00; -fx-min-height: 3; -fx-max-height: 3;");
        divider.setPrefWidth(340);

        //card for login ---------------------------
        VBox card = new VBox(14);
        card.setStyle( //styling
            "-fx-background-color: white; " +
            "-fx-border-color: #003366; -fx-border-width: 2; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; " +
            "-fx-padding: 28;"
        );
        card.setMaxWidth(380);
        card.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("Sign In");
        cardTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #003366;");

        //email field
        Label emailLabel = new Label("Email"); //label
        emailLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        TextField emailField = new TextField(); //text field
        emailField.setPromptText("your@uwindsor.ca");
        emailField.setStyle("-fx-font-size: 13; -fx-padding: 8; -fx-border-color: #D2D2D2; -fx-border-radius: 4; -fx-background-radius: 4;");
        emailField.setMaxWidth(Double.MAX_VALUE);

        //password field
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #282828;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 13; -fx-padding: 8; -fx-border-color: #D2D2D2; -fx-border-radius: 4; -fx-background-radius: 4;");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        //error label in case something goes wrong
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12;");

        //sign in button
        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle(
            "-fx-background-color: #003366; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-font-weight: bold; " +
            "-fx-background-radius: 6; -fx-padding: 10 20 10 20; -fx-cursor: hand;"
        );
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        //link to sign up if user doesn't' have an account yet (button but looks like text)
        Button signUpBtn = new Button("Don't have an account? Sign Up");
        signUpBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #003366; " +
            "-fx-font-size: 12; -fx-cursor: hand; -fx-underline: true;"
        );

        //sign in button action logic ---------------------------
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            //if user hasn't entered their email or password, throw error
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠ Please enter your email and password.");
                return;
            }
            //otherwise, go through users and find by email
            User user = userRepository.findByEmail(email);
            System.out.println("Email entered: " + email); //print to console for debugging
            System.out.println("User found: " + user); //print to console for debugging
            if (user != null) { //if the user exists, compare password entered vs DB password in console
                System.out.println("Password in DB: " + user.getPassword());
                System.out.println("Password entered: " + password);
            }
            //if user isn't found, throw error
            if (user == null) {
                errorLabel.setText("⚠ No account found with that email.");
                return;
            }
            //if password is wrong, throw error
            if (!user.getPassword().equals(password)) {
                errorLabel.setText("⚠ Incorrect password.");
                return;
            }
            //successful
            errorLabel.setText("");
            if (onLoginSuccess != null) onLoginSuccess.accept(user);
        });

        //let the user press enter to sign in instead of clicking button
        passwordField.setOnAction(e -> loginBtn.fire());

        //Sign up link ---------------------------
        signUpBtn.setOnAction(e -> {
            if (onSignUp != null) onSignUp.run();
        });

        //add all components to the card
        card.getChildren().addAll(
            cardTitle, emailLabel, emailField,
            passwordLabel, passwordField,
            errorLabel, loginBtn, signUpBtn
        );

        //return the card view
        view.getChildren().addAll(title, subtitle, divider, card);
        return view;
    }
}