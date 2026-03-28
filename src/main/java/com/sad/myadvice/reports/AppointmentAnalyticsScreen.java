package com.sad.myadvice.reports;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class AppointmentAnalyticsScreen {

    // This method builds the Appointment Analytics page
    // MainController is passed in so the Back button can switch screens
    public VBox build(User student, MainController controller) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Page title
        Label titleLabel = new Label("Appointment Analytics");
        titleLabel.setStyle(UITheme.STYLE_PAGE_TITLE);

        // Card showing students with the highest number of appointments
        VBox studentCard = createAnalyticsCard(
                "Students With the Most Appointments",
                new String[]{"Rank", "Student ID", "Name", "Total Appointments"},
                new String[][]{
                        {"1", "1001", "Ali Khan", "8"},
                        {"2", "1002", "Sara Noor", "6"},
                        {"3", "1003", "Hassan Malik", "5"}
                }
        );

        // Card showing faculty with the highest number of appointments
        VBox facultyCard = createAnalyticsCard(
                "Faculty With the Most Appointments",
                new String[]{"Rank", "Faculty ID", "Name", "Total Appointments"},
                new String[][]{
                        {"1", "F101", "Dr. Ahmed", "12"},
                        {"2", "F104", "Dr. Lee", "10"},
                        {"3", "F102", "Dr. Khan", "7"}
                }
        );

        // Buttons at the bottom of the page
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button backButton = new Button("Back");
        backButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        // Temporary placeholder for refresh logic
        refreshButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Refresh");
            alert.setHeaderText(null);
            alert.setContentText("Analytics refreshed.");
            alert.showAndWait();
        });

        // Back button returns to the Reports Dashboard
        backButton.setOnAction(e -> controller.showReportsDashboard());

        HBox buttonBar = new HBox(18, refreshButton, backButton);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        // Add everything to the main layout
        view.getChildren().addAll(titleLabel, studentCard, facultyCard, buttonBar);
        return view;
    }

    // Helper method to build each analytics card with a title and table
    private VBox createAnalyticsCard(String heading, String[] columns, String[][] data) {
        VBox card = new VBox(UITheme.SPACING);
        card.setStyle(UITheme.STYLE_CARD);

        // Gold accent bar at top of card
        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);

        // Section heading
        Label sectionLabel = new Label(heading);
        sectionLabel.setStyle(UITheme.STYLE_SECTION_LABEL);

        // Table for analytics data
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(180);

        // Dynamically create table columns
        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> column = new TableColumn<>(columns[i]);
            column.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue()[index])
            );
            table.getColumns().add(column);
        }

        // Temporary sample data for layout/testing
        table.setItems(FXCollections.observableArrayList(data));
        table.setStyle(
                "-fx-font-size: " + UITheme.FONT_BODY + ";" +
                "-fx-border-color: " + UITheme.BORDER_GREY + ";" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;"
        );

        card.getChildren().addAll(goldBar, sectionLabel, table);
        return card;
    }
}