package com.sad.myadvice.advising.ui;

import com.sad.myadvice.advising.ui.screens.*;
import com.sad.myadvice.booking.ui.BookingScreen;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Button btnProgress;
    @FXML private Button btnEligible;
    @FXML private Button btnCourseDetails;
    @FXML private Button btnPlans;
    @FXML private Button btnBooking;

    private final UserRepository userRepository;
    private final ProgressScreen progressScreen;
    private final EligibleScreen eligibleScreen;
    private final CourseDetailsScreen courseDetailsScreen;
    private final PlansScreen plansScreen;
    private final BookingScreen bookingScreen;

    private User currentStudent;

    public MainController(UserRepository userRepository, ProgressScreen progressScreen, EligibleScreen eligibleScreen,
                          CourseDetailsScreen courseDetailsScreen, PlansScreen plansScreen, BookingScreen bookingScreen) {
        this.userRepository = userRepository;
        this.progressScreen = progressScreen;
        this.eligibleScreen = eligibleScreen;
        this.courseDetailsScreen = courseDetailsScreen;
        this.plansScreen = plansScreen;
        this.bookingScreen = bookingScreen;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setActiveButton(btnProgress);
        //do not render screens until currentStudent is set to avoid bugs
    }

    public void setCurrentUser(User user) {
        this.currentStudent = user;
        //refresh the current screen with the authenticated user
        showProgress();
    }

    // NAVIGATION -------------------------------------------------------------------

    @FXML
    public void showProgress() {
        setActiveButton(btnProgress);
        setContent(progressScreen.build(currentStudent));
    }

    @FXML
    public void showEligible() {
        setActiveButton(btnEligible);
        setContent(eligibleScreen.build(currentStudent));
    }

    @FXML
    public void showCourseDetails() {
        setActiveButton(btnCourseDetails);
        setContent(courseDetailsScreen.build(currentStudent));
    }

    @FXML
    public void showPlans() {
        setActiveButton(btnPlans);
        setContent(plansScreen.build(currentStudent));
    }

    @FXML
    public void showBooking() {
        setActiveButton(btnBooking);
        setContent(bookingScreen.build(currentStudent));
    }

    // HELPERS -------------------------------------------------------------------
    
    private void setContent(VBox view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void setActiveButton(Button btn) {
        for (Button b : new Button[]{btnProgress, btnEligible, btnCourseDetails, btnPlans}) {
            if (b != null) b.setStyle(UITheme.STYLE_SIDEBAR_BUTTON);
        }
        if (btn != null) btn.setStyle(UITheme.STYLE_SIDEBAR_BUTTON_ACTIVE);
    }
}