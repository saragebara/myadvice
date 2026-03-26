package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class ReportsDashboardScreen {

    // This method builds the main Reports Dashboard UI
    // We pass in the controller so buttons can trigger navigation
    public VBox build(User student, MainController controller) {

        // Main vertical layout for the page
        VBox view = new VBox(20);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Page title
        Label titleLabel = new Label("Reports Dashboard");
        titleLabel.setStyle(UITheme.STYLE_PAGE_TITLE);

        // Grid for summary cards (2x2 layout)
        GridPane cardsPanel = new GridPane();
        cardsPanel.setHgap(20);
        cardsPanel.setVgap(20);

        // Adding 4 summary cards
        cardsPanel.add(createSummaryCard("Total Students", "250"), 0, 0);
        cardsPanel.add(createSummaryCard("Total Faculty", "40"), 1, 0);
        cardsPanel.add(createSummaryCard("Total Appointments", "120"), 0, 1);
        cardsPanel.add(createSummaryCard("Most Booked Faculty", "Dr. Ahmed"), 1, 1);

        // Grid for navigation buttons
        GridPane buttonPanel = new GridPane();
        buttonPanel.setHgap(15);
        buttonPanel.setVgap(15);

        // Creating buttons
        Button studentsButton = new Button("View Students Report");
        studentsButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button facultyButton = new Button("View Faculty Report");
        facultyButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button analyticsButton = new Button("Appointment Analytics");
        analyticsButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button backButton = new Button("Back");
        backButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        // Adding buttons to grid
        buttonPanel.add(studentsButton, 0, 0);
        buttonPanel.add(facultyButton, 1, 0);
        buttonPanel.add(analyticsButton, 0, 1);
        buttonPanel.add(backButton, 1, 1);

        // Navigation logic using MainController
        // Instead of opening new windows, we swap content inside the main layout
        studentsButton.setOnAction(e -> controller.showStudentsReport());
        facultyButton.setOnAction(e -> controller.showFacultyReport());
        analyticsButton.setOnAction(e -> controller.showAppointmentAnalytics());
        backButton.setOnAction(e -> controller.showProgress());

        // Add everything to main layout
        view.getChildren().addAll(titleLabel, cardsPanel, buttonPanel);

        return view;
    }

    // Helper method to create a styled summary card
    private VBox createSummaryCard(String title, String value) {

        VBox card = new VBox(10);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPrefWidth(300);
        card.setMinHeight(140);

        // Gold accent bar at top of card
        Region accentBar = new Region();
        accentBar.setStyle(UITheme.STYLE_GOLD_BAR);

        // Card title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(UITheme.STYLE_SECTION_LABEL);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Card value (big number/text)
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: " + UITheme.TEXT_DARK + ";");
        valueLabel.setAlignment(Pos.CENTER);
        valueLabel.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(valueLabel, Priority.ALWAYS);

        // Padding inside card
        card.setPadding(new Insets(0, UITheme.CARD_PADDING, UITheme.CARD_PADDING, UITheme.CARD_PADDING));

        // Add elements to card
        card.getChildren().addAll(accentBar, titleLabel, valueLabel);

        return card;
    }
}