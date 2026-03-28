package com.sad.myadvice;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.reports.AppointmentAnalyticsScreen;
import com.sad.myadvice.reports.FacultyReportScreen;
import com.sad.myadvice.reports.HomeFacultyScreen;
import com.sad.myadvice.reports.HomeStudentScreen;
import com.sad.myadvice.reports.ReportsDashboardScreen;
import com.sad.myadvice.reports.StudentsReportScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PreviewLauncher extends Application {

    private StackPane root;
    private PreviewController controller;
    private User fakeUser;

    @Override
    public void start(Stage stage) {
        root = new StackPane();
        controller = new PreviewController();
        fakeUser = new User();

        showFacultyHomeView(); // change to showHomeView() if you want student first

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Home + Reports Preview");
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setView(VBox view) {
        root.getChildren().setAll(view);
    }

    private void showHomeView() {
        HomeStudentScreen screen = new HomeStudentScreen();
        setView(screen.build(fakeUser, controller));
    }

    private void showFacultyHomeView() {
        HomeFacultyScreen screen = new HomeFacultyScreen();
        setView(screen.build(fakeUser, controller));
    }

    private void showReportsDashboardView() {
        ReportsDashboardScreen screen = new ReportsDashboardScreen();
        setView(screen.build(fakeUser, controller));
    }

    private void showStudentsReportView() {
        StudentsReportScreen screen = new StudentsReportScreen();
        setView(screen.build(fakeUser, controller));
    }

    private void showFacultyReportView() {
        FacultyReportScreen screen = new FacultyReportScreen();
        setView(screen.build(fakeUser, controller));
    }

    private void showAppointmentAnalyticsView() {
        AppointmentAnalyticsScreen screen = new AppointmentAnalyticsScreen();
        setView(screen.build(fakeUser, controller));
    }

    class PreviewController extends MainController {

        public PreviewController() {
            super(null, null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public void showHome() {
            PreviewLauncher.this.showHomeView();
        }

        @Override
        public void showFacultyHome() {
            PreviewLauncher.this.showFacultyHomeView();
        }

        @Override
        public void showReportsDashboard() {
            PreviewLauncher.this.showReportsDashboardView();
        }

        @Override
        public void showStudentsReport() {
            PreviewLauncher.this.showStudentsReportView();
        }

        @Override
        public void showFacultyReport() {
            PreviewLauncher.this.showFacultyReportView();
        }

        @Override
        public void showAppointmentAnalytics() {
            PreviewLauncher.this.showAppointmentAnalyticsView();
        }

        @Override
        public void showProgress() {
            // not used in this preview
        }

        @Override
        public void showEligible() {
            // not used in this preview
        }

        @Override
        public void showCourseDetails() {
            // not used in this preview
        }

        @Override
        public void showPlans() {
            // not used in this preview
        }
    }
}
