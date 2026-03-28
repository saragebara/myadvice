package com.sad.myadvice.reports;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;

@Component
public class HomeStudentScreen {

    public VBox build(User student, MainController controller) {

        VBox view = new VBox(30);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: " + UITheme.UW_BLUE + ";");

        // ---------------- TOP BAR ----------------
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(10, 0, 10, 0));

        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER_LEFT);

        // load logo
        Image logoImage = new Image(getClass().getResourceAsStream("/logoCrop.png"));
        ImageView logoView = new ImageView(logoImage);

        // logo size
        logoView.setFitHeight(75);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        logoBox.getChildren().add(logoView);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);
        logoutButton.setPrefHeight(36);

        HBox logoutBox = new HBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        topBar.setLeft(logoBox);
        topBar.setRight(logoutBox);

        // ---------------- MAIN DASHBOARD PANEL ----------------
        VBox dashboardCard = new VBox(20);
        dashboardCard.setPadding(new Insets(35));
        dashboardCard.setStyle(
                "-fx-background-color: " + UITheme.LIGHT_GREY + "; " +
                "-fx-border-color: #D9E1EA; " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 14; " +
                "-fx-background-radius: 14;"
        );

        Label welcomeLabel = new Label("Welcome Back!");
        welcomeLabel.setStyle(
                "-fx-font-size: 22; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + UITheme.UW_BLUE + ";"
        );
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.setAlignment(Pos.CENTER);

        Region topAccentBar = new Region();
        topAccentBar.setStyle(UITheme.STYLE_GOLD_BAR);
        topAccentBar.setMaxWidth(Double.MAX_VALUE);

        Label dashboardTitle = new Label("Student Dashboard");
        dashboardTitle.setStyle(
                "-fx-font-size: 22; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + UITheme.UW_BLUE + ";"
        );

        Label dashboardDescription = new Label(
                "Plan your academic journey, manage your schedule, and connect with faculty — all in one place."
        );
        dashboardDescription.setStyle(
                "-fx-font-size: 14; " +
                "-fx-text-fill: " + UITheme.TEXT_DARK + ";"
        );
        dashboardDescription.setWrapText(true);

        // ---------------- GRID ----------------
        GridPane cardsPanel = new GridPane();
        cardsPanel.setHgap(20);
        cardsPanel.setVgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        col.setHgrow(Priority.ALWAYS);

        cardsPanel.getColumnConstraints().addAll(col, col);

        cardsPanel.add(createDashboardCard(
                "Curriculum Advising",
                "Explore your degree requirements, track completed courses, and plan future semesters."
        ), 0, 0);

        cardsPanel.add(createDashboardCard(
                "Scheduling",
                "Create your semester timetable by selecting courses, sections, and instructors that fit your availability."
        ), 1, 0);

        cardsPanel.add(createDashboardCard(
                "Booking",
                "Book appointments with faculty advisors for academic guidance, course planning, or project supervision."
        ), 0, 1);

        cardsPanel.add(createClickableReportsCard(
                "Reports",
                "View insights about your academic progress, completed courses, and advising activity.",
                () -> controller.showReportsDashboard()
        ), 1, 1);

        dashboardCard.getChildren().addAll(
                welcomeLabel,
                topAccentBar,
                dashboardTitle,
                dashboardDescription,
                cardsPanel
        );

        view.getChildren().addAll(topBar, dashboardCard);

        return view;
    }

    private VBox createDashboardCard(String title, String description) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setMinHeight(190);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.CENTER);
        card.setStyle(getNormalCardStyle());

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER);

        titleBox.setMinHeight(40);
        titleBox.setPrefHeight(40);
        titleBox.setMaxHeight(40);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 17; " +
                "-fx-font-weight: 600; " +
                "-fx-text-fill: " + UITheme.UW_BLUE + ";"
        );

        String iconPath = getIconPath(title);

        if (iconPath != null) {
            Image iconImage = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iconView = new ImageView(iconImage);

            // adjust size per icon
            double size = 28;

            if (title.equals("Curriculum Advising")) {
                size = 36; // cap needs a boost
            }

            iconView.setFitWidth(size);
            iconView.setFitHeight(size);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);

            titleBox.getChildren().addAll(iconView, titleLabel);
        } else {
            titleBox.getChildren().add(titleLabel);
        }

        VBox.setMargin(titleBox, new Insets(0, 0, 4, 0));

        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);
        goldBar.setPrefHeight(5);
        goldBar.setPrefWidth(110);

        VBox.setMargin(goldBar, new Insets(0, 0, 10, 0));

        Label descLabel = new Label(description);
        descLabel.setStyle(
                "-fx-font-size: 14; " +
                "-fx-text-fill: " + UITheme.TEXT_DARK + ";"
        );
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(Double.MAX_VALUE);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setTextAlignment(TextAlignment.CENTER);

        VBox.setVgrow(descLabel, Priority.ALWAYS);

        card.getChildren().addAll(titleBox, goldBar, descLabel);

        return card;
    }

    private String getIconPath(String title) {
        switch (title) {
            case "Curriculum Advising":
                return "/gradCap.png";
            case "Scheduling":
                return "/calendar.png";
            case "Booking":
                return "/clock.png";
            case "Reports":
                return "/barChart.png";
            default:
                return null;
        }
    }

    private VBox createClickableReportsCard(String title, String description, Runnable action) {
        VBox card = createDashboardCard(title, description);

        card.setCursor(Cursor.HAND);

        card.setOnMouseEntered(e -> {
            card.setStyle(getReportsHoverStyle());
            card.setTranslateY(-2);
        });

        card.setOnMouseExited(e -> {
            card.setStyle(getNormalCardStyle());
            card.setTranslateY(0);
        });

        card.setOnMouseClicked(e -> action.run());

        return card;
    }

    private String getNormalCardStyle() {
        return "-fx-background-color: " + UITheme.WHITE + "; " +
                "-fx-border-color: #D9E1EA; " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 14; " +
                "-fx-background-radius: 14;";
    }

    private String getReportsHoverStyle() {
        return "-fx-background-color: #EAF2FB; " +
                "-fx-border-color: " + UITheme.UW_BLUE + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 14; " +
                "-fx-background-radius: 14;";
    }
}