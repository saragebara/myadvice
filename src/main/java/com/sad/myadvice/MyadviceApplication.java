package com.sad.myadvice;

import com.sad.myadvice.ui.LoginScreen;
import com.sad.myadvice.ui.SignupScreen;
import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.entity.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.fxml.FXMLLoader;

@SpringBootApplication
public class MyadviceApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(MyadviceApplication.class);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("myAdvice");
        stage.setWidth(1000);
        stage.setHeight(680);
        showLogin();
        stage.show();
    }

    //LOGIN SCREEN ---------------------------------------------------------
    private void showLogin() {
        LoginScreen loginScreen = springContext.getBean(LoginScreen.class);

        loginScreen.setOnLoginSuccess(user -> showDashboard(user));
        loginScreen.setOnSignUp(() -> showSignup());

        VBox view = loginScreen.build();
        primaryStage.setScene(new Scene(view, 1000, 680));
    }

    //SIGNUP SCREEN ---------------------------------------------------------
    private void showSignup() {
        SignupScreen signupScreen = springContext.getBean(SignupScreen.class);

        signupScreen.setOnSignupSuccess(user -> showDashboard(user));
        signupScreen.setOnBackToLogin(() -> showLogin());

        VBox view = signupScreen.build();
        primaryStage.setScene(new Scene(view, 1000, 680));
    }

    //DASHBOARD BASED ON ROLE (STUDENT, FACULTY, STAFF)
    private void showDashboard(User user) {
        try {
            //debug line
            System.out.println("Loading dashboard for: " + user.getName() + " | Major: " + user.getMajor());

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            //loading fxml first
            javafx.scene.Parent root = loader.load();
            //setting the user
            MainController controller = loader.getController();
            controller.setCurrentUser(user);

            primaryStage.setScene(new Scene(root, 1000, 680));
        } catch (Exception e) {
            e.printStackTrace();
            //debug
            System.out.println("Dashboard failed to load: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        springContext.close();
    }
}