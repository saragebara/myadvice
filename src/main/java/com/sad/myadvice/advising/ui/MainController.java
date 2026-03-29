package com.sad.myadvice.advising.ui;

import com.sad.myadvice.administering.ui.screens.*;
import com.sad.myadvice.advising.ui.screens.*;
import com.sad.myadvice.booking.ui.screens.*;
import com.sad.myadvice.reports.ui.screens.*;
import com.sad.myadvice.scheduling.ui.*;
import com.sad.myadvice.entity.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private VBox sidebarButtons;
    @FXML private Label sidebarRoleLabel;
    @FXML private Label sidebarUserLabel;

    // STUDENT SCREENS ----------------------------------------------------------
    private final ProgressScreen progressScreen;
    private final EligibleScreen eligibleScreen;
    private final CourseDetailsScreen courseDetailsScreen;
    private final PlansScreen plansScreen;
    private final BookingScreen bookingScreen;
    private final SchedullingScreen schedullingScreen;

    // FACULTY SCREENS ----------------------------------------------------------
    private final FacultyAppointmentRequestsScreen facultyRequestsScreen;
    private final FacultyMyAppointmentsScreen facultyAppointmentsScreen;
    private final FacultyAvailabilityScreen facultyAvailabilityScreen;
    private final FacultyStudentProgressScreen facultyStudentProgressScreen;
    private final FacultyCoursePlanScreen facultyCoursePlanScreen;

    // STAFF SCREENS ------------------------------------------------------------
    private final StaffAllAppointmentsScreen staffAppointmentsScreen;
    private final EditableTimetableScreen editableTimetableScreen;
    private final EditablePrerequisitesScreen editablePrerequisitesScreen;
    private final EditableResearchAreasScreen editableResearchAreasScreen;

    // SHARED SCREENS -----------------------------------------------------------
    private final ReportsDashboardScreen reportsDashboardScreen;
    private final StudentsReportScreen studentsReportScreen;
    private final FacultyReportScreen facultyReportScreen;
    private final AppointmentAnalyticsScreen appointmentAnalyticsScreen;

    private User currentUser;
    private Button activeButton;

    public MainController(ProgressScreen progressScreen,
            EligibleScreen eligibleScreen,
            CourseDetailsScreen courseDetailsScreen,
            PlansScreen plansScreen,
            BookingScreen bookingScreen,
            FacultyAppointmentRequestsScreen facultyRequestsScreen,
            FacultyMyAppointmentsScreen facultyAppointmentsScreen,
            FacultyAvailabilityScreen facultyAvailabilityScreen,
            FacultyCoursePlanScreen facultyCoursePlanScreen,
            FacultyStudentProgressScreen facultyStudentProgressScreen,
            StaffAllAppointmentsScreen staffAppointmentsScreen,
            ReportsDashboardScreen reportsDashboardScreen,
            StudentsReportScreen studentsReportScreen,
            FacultyReportScreen facultyReportScreen,
            SchedullingScreen schedullingScreen,
            AppointmentAnalyticsScreen appointmentAnalyticsScreen,
            EditableTimetableScreen editableTimetableScreen,
            EditablePrerequisitesScreen editablePrerequisitesScreen,
            EditableResearchAreasScreen editableResearchAreasScreen) {

        this.progressScreen = progressScreen;
        this.eligibleScreen = eligibleScreen;
        this.courseDetailsScreen = courseDetailsScreen;
        this.plansScreen = plansScreen;
        this.bookingScreen = bookingScreen;
        this.facultyRequestsScreen = facultyRequestsScreen;
        this.facultyAppointmentsScreen = facultyAppointmentsScreen;
        this.facultyAvailabilityScreen = facultyAvailabilityScreen;
        this.facultyCoursePlanScreen = facultyCoursePlanScreen;
        this.facultyStudentProgressScreen = facultyStudentProgressScreen;
        this.staffAppointmentsScreen = staffAppointmentsScreen;
        this.reportsDashboardScreen = reportsDashboardScreen;
        this.studentsReportScreen = studentsReportScreen;
        this.facultyReportScreen = facultyReportScreen;
        this.appointmentAnalyticsScreen = appointmentAnalyticsScreen;
        this.schedullingScreen = schedullingScreen;
        this.editableTimetableScreen = editableTimetableScreen;
        this.editablePrerequisitesScreen = editablePrerequisitesScreen;
        this.editableResearchAreasScreen = editableResearchAreasScreen;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Sidebar is built dynamically in setCurrentUser() instead of initially
    }

    //setting current user and getting their role
    public void setCurrentUser(User user) {
        this.currentUser = user;
        sidebarUserLabel.setText(user.getName() + "  •  " + user.getRole());

        //switching portal based on user's role
        switch (user.getRole()) {
            case STUDENT -> {
                sidebarRoleLabel.setText("Student Portal");
                buildStudentSidebar();
                showProgress();
            }
            case FACULTY -> {
                sidebarRoleLabel.setText("Faculty Portal");
                buildFacultySidebar();
                showAppointmentRequests();
            }
            case STAFF -> {
                sidebarRoleLabel.setText("Staff Portal");
                buildStaffSidebar();
                showReportsDashboard();
            }
        }
    }

    //SIDEBAR BUILDERS

    private void buildStudentSidebar() {
        sidebarButtons.getChildren().clear();
        sidebarButtons.getChildren().addAll(
            navButton("📊  My Progress", this::showProgress),
            navButton("✅  Eligible Courses", this::showEligible),
            navButton("🔍  Course Details", this::showCourseDetails),
            navButton("📋  My Plans", this::showPlans),
            navButton("📅  Scheduling", this::showSchedulling),
            navButton("⌚  Book Appointment", this::showBooking),
            navButton("📊  Reports", this::showReportsDashboard)
        );
    }

    private void buildFacultySidebar() {
        sidebarButtons.getChildren().clear();
        sidebarButtons.getChildren().addAll(
            navButton("📬  Appointment Requests", this::showAppointmentRequests),
            navButton("📆  My Appointments", this::showFacultyAppointments),
            navButton("🕐  My Availability", this::showFacultyAvailability),
            navButton("📊  Reports", this::showReportsDashboard),
            navButton("📈  Student Progress", this::showFacultyStudentProgress),
            navButton("📋  Review Plans", this::showFacultyCoursePlans)
        );
    }

    private void buildStaffSidebar() {
        sidebarButtons.getChildren().clear();
        sidebarButtons.getChildren().addAll(
            navButton("📆  All Appointments", this::showStaffAppointments),
            navButton("📊  Reports", this::showReportsDashboard),
            navButton("🗓️  Edit Timetable",       this::showEditTimetable),
            navButton("📚  Edit Prerequisites",   this::showEditPrerequisites),
            navButton("🔬  Research Areas",       this::showEditResearchAreas)
        );
    }

    //NAVIGATION METHOSD ----------------------------------------------------------

    //STUDENT ----------------------------------------------
    public void showProgress() { //CURR ADVISING - Degree requirement progress
        setContent(progressScreen.build(currentUser));
    }

    public void showEligible() { //CURR ADVISING - Eligible courses to take
        setContent(eligibleScreen.build(currentUser));
    }

    public void showCourseDetails() { //CURR ADVISING - Course details
        setContent(courseDetailsScreen.build(currentUser));
    }

    public void showPlans() { //CURR ADVISING - Future Course planning
        setContent(plansScreen.build(currentUser));
    }

    public void showBooking() { //BOOKING - Book appointments with faculty
        setContent(bookingScreen.build(currentUser));
    }

    public void showSchedulling(){ //SCHEDULING - Manage courses for upcoming semester
        setContent(schedullingScreen.build(currentUser));
    }

    //FACULTY ----------------------------------------------
    public void showAppointmentRequests() {
        setContent(facultyRequestsScreen.build(currentUser));
    }

    public void showFacultyAppointments() {
        setContent(facultyAppointmentsScreen.build(currentUser));
    }

    public void showFacultyAvailability() {
        setContent(facultyAvailabilityScreen.build(currentUser));
    }

    public void showFacultyStudentProgress() { 
        setContent(facultyStudentProgressScreen.build(currentUser)); 
    }
    public void showFacultyCoursePlans() { 
        setContent(facultyCoursePlanScreen.build(currentUser)); 
    }

    //STAFF ------------------------------------------------
    public void showStaffAppointments() {
        setContent(staffAppointmentsScreen.build(currentUser));
    }

    public void showEditTimetable()     { 
        setContent(editableTimetableScreen.build()); 
    }
    public void showEditPrerequisites() { 
        setContent(editablePrerequisitesScreen.build()); 
    }
    public void showEditResearchAreas() { 
        setContent(editableResearchAreasScreen.build()); 
    }

    //Shared - REPORTS ----------------------------------------------
    // TO-DO UPDATE THIS TO BE DIFFERENT VIEWS
    public void showReportsDashboard() {
        setContent(reportsDashboardScreen.build(currentUser, this));
    }

    public void showStudentsReport() {
        setContent(studentsReportScreen.build(currentUser, this));
    }

    public void showFacultyReport() {
        setContent(facultyReportScreen.build(currentUser, this));
    }

    public void showAppointmentAnalytics() {
        setContent(appointmentAnalyticsScreen.build(currentUser, this));
    }

    //Helpers ------------------------------------------------------------------------

    private void setContent(VBox view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    //nav button styling and action
    private Button navButton(String text, Runnable action) {
        Button b = new Button(text);
        b.setStyle(UITheme.STYLE_SIDEBAR_BUTTON);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> {
            setActiveButton(b);
            action.run();
        });
        return b;
    }
    //setting active button to be styled
    private void setActiveButton(Button btn) {
        if (activeButton != null) {
            activeButton.setStyle(UITheme.STYLE_SIDEBAR_BUTTON);
        }
        btn.setStyle(UITheme.STYLE_SIDEBAR_BUTTON_ACTIVE);
        activeButton = btn;
    }
}