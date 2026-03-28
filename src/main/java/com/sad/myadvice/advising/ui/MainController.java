package com.sad.myadvice.advising.ui;

import com.sad.myadvice.advising.ui.screens.*;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.reports.AppointmentAnalyticsScreen;
import com.sad.myadvice.reports.FacultyReportScreen;
import com.sad.myadvice.reports.HomeFacultyScreen;
import com.sad.myadvice.reports.HomeStudentScreen;
import com.sad.myadvice.reports.ReportsDashboardScreen;
import com.sad.myadvice.reports.StudentsReportScreen;
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

    private final UserRepository userRepository;
    private final ProgressScreen progressScreen;
    private final EligibleScreen eligibleScreen;
    private final CourseDetailsScreen courseDetailsScreen;
    private final PlansScreen plansScreen;

    private final ReportsDashboardScreen reportsDashboardScreen;
    private final StudentsReportScreen studentsReportScreen;
    private final FacultyReportScreen facultyReportScreen;
    private final AppointmentAnalyticsScreen appointmentAnalyticsScreen;

    private final HomeStudentScreen homeStudentScreen;
    private final HomeFacultyScreen homeFacultyScreen;

    private User currentStudent;

    public MainController(UserRepository userRepository,
                          HomeStudentScreen homeStudentScreen,
                          HomeFacultyScreen homeFacultyScreen,
                          ProgressScreen progressScreen,
                          EligibleScreen eligibleScreen,
                          CourseDetailsScreen courseDetailsScreen,
                          PlansScreen plansScreen,
                          ReportsDashboardScreen reportsDashboardScreen,
                          StudentsReportScreen studentsReportScreen,
                          FacultyReportScreen facultyReportScreen,
                          AppointmentAnalyticsScreen appointmentAnalyticsScreen) {
        this.userRepository = userRepository;
        this.homeStudentScreen = homeStudentScreen;
        this.homeFacultyScreen = homeFacultyScreen;
        this.progressScreen = progressScreen;
        this.eligibleScreen = eligibleScreen;
        this.courseDetailsScreen = courseDetailsScreen;
        this.plansScreen = plansScreen;
        this.reportsDashboardScreen = reportsDashboardScreen;
        this.studentsReportScreen = studentsReportScreen;
        this.facultyReportScreen = facultyReportScreen;
        this.appointmentAnalyticsScreen = appointmentAnalyticsScreen;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentStudent = userRepository.findByStudentId("110177359");
        showHome();
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

    // Loads the Reports Dashboard screen into the main content area
    @FXML
    public void showReportsDashboard() {
        setContent(reportsDashboardScreen.build(currentStudent, this));
    }

    // Loads the Students Report screen into the main content area
    @FXML
    public void showStudentsReport() {
        setContent(studentsReportScreen.build(currentStudent, this));
    }

    // Loads the Faculty Report screen into the main content area
    @FXML
    public void showFacultyReport() {
        setContent(facultyReportScreen.build(currentStudent, this));
    }

    // Loads the Appointment Analytics screen into the main content area
    @FXML
    public void showAppointmentAnalytics() {
        setContent(appointmentAnalyticsScreen.build(currentStudent, this));
    }

    @FXML
    public void showHome() {
        setActiveButton(null);
        setContent(homeStudentScreen.build(currentStudent, this));
    }

    @FXML
    public void showFacultyHome() {
        setActiveButton(null);
        setContent(homeFacultyScreen.build(currentStudent, this));
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