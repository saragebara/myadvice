package com.sad.myadvice.advising.ui;

import com.sad.myadvice.advising.ui.screens.*;
import com.sad.myadvice.booking.ui.BookingScreen;
import com.sad.myadvice.reports.ui.screens.*;
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
    @FXML private Button btnReportDash;
    @FXML private Button btnStudentReport;
    @FXML private Button btnFacultyReport;
    @FXML private Button btnApptAnalytics;

    //curriculum advising screens
    private final UserRepository userRepository;
    private final ProgressScreen progressScreen;
    private final EligibleScreen eligibleScreen;
    private final CourseDetailsScreen courseDetailsScreen;
    private final PlansScreen plansScreen;

    //booking screen
    private final BookingScreen bookingScreen;

    //reports screens
    private final ReportsDashboardScreen reportsDashboardScreen;
    private final StudentsReportScreen studentsReportScreen;
    private final FacultyReportScreen facultyReportScreen;
    private final AppointmentAnalyticsScreen appointmentAnalyticsScreen;

    private User currentStudent;

    public MainController(UserRepository userRepository, ProgressScreen progressScreen, EligibleScreen eligibleScreen,
                          CourseDetailsScreen courseDetailsScreen, PlansScreen plansScreen, BookingScreen bookingScreen,
                          ReportsDashboardScreen reportsDashboardScreen, StudentsReportScreen studentsReportScreen,
                          FacultyReportScreen facultyReportScreen,  AppointmentAnalyticsScreen appointmentAnalyticsScreen) {
        this.userRepository = userRepository;
        this.progressScreen = progressScreen;
        this.eligibleScreen = eligibleScreen;
        this.courseDetailsScreen = courseDetailsScreen;
        this.plansScreen = plansScreen;
        this.bookingScreen = bookingScreen;
        this.reportsDashboardScreen = reportsDashboardScreen;
        this.studentsReportScreen = studentsReportScreen;
        this.facultyReportScreen = facultyReportScreen;
        this.appointmentAnalyticsScreen = appointmentAnalyticsScreen;
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

    //-- CURRICULUM ADVISING ---------------------------------
    //show current degree progress pane
    @FXML 
    public void showProgress() {
        setActiveButton(btnProgress);
        setContent(progressScreen.build(currentStudent));
    }
    //show eligible courses pane
    @FXML
    public void showEligible() {
        setActiveButton(btnEligible);
        setContent(eligibleScreen.build(currentStudent));
    }
    //show course details pane
    @FXML
    public void showCourseDetails() {
        setActiveButton(btnCourseDetails);
        setContent(courseDetailsScreen.build(currentStudent));
    }
    //show course plan pane
    @FXML
    public void showPlans() {
        setActiveButton(btnPlans);
        setContent(plansScreen.build(currentStudent));
    }
    //-- BOOKINGS ---------------------------------------------
    //show bookings pane
    @FXML
    public void showBooking() {
        setActiveButton(btnBooking);
        setContent(bookingScreen.build(currentStudent));
    }

    //-- REPORTS ----------------------------------------------
    // Loads the Reports Dashboard screen into the main content area
    @FXML
    public void showReportsDashboard() {
        setActiveButton(btnReportDash);
        setContent(reportsDashboardScreen.build(currentStudent, this));
    }

    // Loads the Students Report screen into the main content area
    @FXML
    public void showStudentsReport() {
        setActiveButton(btnStudentReport);
        setContent(studentsReportScreen.build(currentStudent, this));
    }

    // Loads the Faculty Report screen into the main content area
    @FXML
    public void showFacultyReport() {
        setActiveButton(btnFacultyReport);
        setContent(facultyReportScreen.build(currentStudent, this));
    }

    // Loads the Appointment Analytics screen into the main content area
    @FXML
    public void showAppointmentAnalytics() {
        setActiveButton(btnApptAnalytics);
        setContent(appointmentAnalyticsScreen.build(currentStudent, this));
    }

    //-----------------------------------------------------------------------------

    // HELPERS 
    
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