package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import org.springframework.stereotype.Component;

@Component
public class HomeScreen {

    public VBox build(User student, MainController controller) {

        VBox view = new VBox(25);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: " + UITheme.UW_BLUE + ";");

        // ---------------- TOP SECTION ----------------
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(10, 10, 20, 10));

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Circle logoCircle = new Circle(22);
        logoCircle.setStyle("-fx-fill: " + UITheme.WHITE + ";");

        Label logoText = new Label("Logo");
        logoText.setStyle(
                "-fx-font-size: " + UITheme.FONT_BODY + ";" +
                "-fx-text-fill: " + UITheme.WHITE + ";"
        );

        logoBox.getChildren().addAll(logoCircle, logoText);

        Label welcomeLabel = new Label("Welcome to myAdvice!");
        welcomeLabel.setStyle(
                "-fx-font-size: 28; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + UITheme.WHITE + ";"
        );

        BorderPane.setAlignment(welcomeLabel, Pos.CENTER);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);
        logoutButton.setPrefWidth(100);

        HBox logoutBox = new HBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        topBar.setLeft(logoBox);
        topBar.setCenter(welcomeLabel);
        topBar.setRight(logoutBox);

        // ---------------- DASHBOARD SECTION ----------------
        VBox dashboardCard = new VBox(18);
        dashboardCard.setStyle(UITheme.STYLE_CARD);
        dashboardCard.setPadding(new Insets(20));

        Region topAccentBar = new Region();
        topAccentBar.setStyle(UITheme.STYLE_GOLD_BAR);
        topAccentBar.setMaxWidth(Double.MAX_VALUE);

        Label dashboardTitle = new Label("Student Dashboard");
        dashboardTitle.setStyle(UITheme.STYLE_SECTION_LABEL + "; -fx-font-size: 18;");

        Label dashboardDescription = new Label(
                "myAdvice helps students manage their academic journey by supporting curriculum advising, scheduling, appointment booking, and reporting tools all in one place."
        );
        dashboardDescription.setStyle(UITheme.STYLE_BODY_LABEL);
        dashboardDescription.setWrapText(true);

        GridPane cardsPanel = new GridPane();
        cardsPanel.setHgap(20);
        cardsPanel.setVgap(20);
        cardsPanel.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        cardsPanel.getColumnConstraints().addAll(col1, col2);

        cardsPanel.add(createDashboardCard(
                "Curriculum Advising",
                "View academic guidance and explore course planning support."
        ), 0, 0);

        cardsPanel.add(createDashboardCard(
                "Scheduling",
                "Organize and manage important academic schedules and timelines."
        ), 1, 0);

        cardsPanel.add(createDashboardCard(
                "Booking",
                "Access appointment booking tools for advising and student support."
        ), 0, 1);

        cardsPanel.add(createClickableDashboardCard(
                "Reports",
                "View report dashboards and access important student information.",
                () -> controller.showReportsDashboard()
        ), 1, 1);

        dashboardCard.getChildren().addAll(
                topAccentBar,
                dashboardTitle,
                dashboardDescription,
                cardsPanel
        );

        view.getChildren().addAll(topBar, dashboardCard);

        return view;
    }

    private VBox createDashboardCard(String title, String description) {
        VBox card = new VBox(10);
        card.setStyle(getNormalCardStyle());
        card.setPrefWidth(300);
        card.setMinHeight(150);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(UITheme.CARD_PADDING));

        Label titleLabel = new Label(title);
        titleLabel.setStyle(UITheme.STYLE_SECTION_LABEL);
        titleLabel.setWrapText(true);

        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);
        goldBar.setPrefHeight(4);
        goldBar.setMaxWidth(Double.MAX_VALUE);

        Label descLabel = new Label(description);
        descLabel.setStyle(UITheme.STYLE_BODY_LABEL);
        descLabel.setWrapText(true);

        VBox.setVgrow(descLabel, Priority.ALWAYS);

        // IMPORTANT ORDER: title -> gold bar -> description
        card.getChildren().addAll(
                titleLabel,
                goldBar,
                descLabel
        );

        return card;
    }

    private VBox createClickableDashboardCard(String title, String description, Runnable action) {
        VBox card = createDashboardCard(title, description);

        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(e -> action.run());
        card.setOnMouseEntered(e -> card.setStyle(getHoverCardStyle()));
        card.setOnMouseExited(e -> card.setStyle(getNormalCardStyle()));

        return card;
    }

    private String getNormalCardStyle() {
        return "-fx-background-color: " + UITheme.WHITE + "; " +
                "-fx-border-color: " + UITheme.UW_BLUE + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;";
    }

    private String getHoverCardStyle() {
        return "-fx-background-color: " + UITheme.SELECTED_BLUE + "; " +
                "-fx-border-color: " + UITheme.UW_BLUE + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;";
    }
}