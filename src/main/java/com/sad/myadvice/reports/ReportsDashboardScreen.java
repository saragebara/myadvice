package com.sad.myadvice.reports;

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

    public VBox build(User student, MainController controller) {

        VBox view = new VBox(20);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);
        view.setPadding(new Insets(30));
        view.setFillWidth(true);

        Label titleLabel = new Label("Reports Dashboard");
        titleLabel.setStyle(UITheme.STYLE_PAGE_TITLE);

        // ---------------- SUMMARY CARDS ----------------
        GridPane cardsPanel = new GridPane();
        cardsPanel.setHgap(20);
        cardsPanel.setVgap(20);
        cardsPanel.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);

        cardsPanel.getColumnConstraints().addAll(col1, col2);

        VBox card1 = createSummaryCard("Total Students", "250");
        VBox card2 = createSummaryCard("Total Faculty", "40");
        VBox card3 = createSummaryCard("Total Appointments", "120");
        VBox card4 = createSummaryCard("Most Booked Faculty", "Dr. Ahmed");

        GridPane.setHgrow(card1, Priority.ALWAYS);
        GridPane.setHgrow(card2, Priority.ALWAYS);
        GridPane.setHgrow(card3, Priority.ALWAYS);
        GridPane.setHgrow(card4, Priority.ALWAYS);

        cardsPanel.add(card1, 0, 0);
        cardsPanel.add(card2, 1, 0);
        cardsPanel.add(card3, 0, 1);
        cardsPanel.add(card4, 1, 1);

        // ---------------- BUTTONS ----------------
        FlowPane buttonPanel = new FlowPane();
        buttonPanel.setHgap(15);
        buttonPanel.setVgap(15);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setMaxWidth(Double.MAX_VALUE);

        Button studentsButton = new Button("View Students Report");
        Button facultyButton = new Button("View Faculty Report");
        Button analyticsButton = new Button("Appointment Analytics");
        Button backButton = new Button("Back");

        studentsButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        facultyButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        analyticsButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);
        backButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        studentsButton.setPrefWidth(180);
        facultyButton.setPrefWidth(180);
        analyticsButton.setPrefWidth(180);
        backButton.setPrefWidth(90);

        addHoverEffect(studentsButton, true);
        addHoverEffect(facultyButton, true);
        addHoverEffect(analyticsButton, true);
        addHoverEffect(backButton, false);

        studentsButton.setOnAction(e -> controller.showStudentsReport());
        facultyButton.setOnAction(e -> controller.showFacultyReport());
        analyticsButton.setOnAction(e -> controller.showAppointmentAnalytics());
        backButton.setOnAction(e -> controller.showHome());

        buttonPanel.getChildren().addAll(studentsButton, facultyButton, analyticsButton, backButton);

        VBox.setVgrow(cardsPanel, Priority.ALWAYS);

        view.getChildren().addAll(titleLabel, cardsPanel, buttonPanel);

        return view;
    }

    private VBox createSummaryCard(String title, String value) {

        VBox card = new VBox(12);
        card.setStyle(UITheme.STYLE_CARD);
        card.setPadding(new Insets(0, UITheme.CARD_PADDING, UITheme.CARD_PADDING, UITheme.CARD_PADDING));
        card.setMinHeight(160);
        card.setMaxWidth(Double.MAX_VALUE);

        Region accentBar = new Region();
        accentBar.setStyle(UITheme.STYLE_GOLD_BAR);
        accentBar.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 15; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + UITheme.UW_BLUE + ";"
        );
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 26; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + UITheme.TEXT_DARK + ";"
        );
        valueLabel.setAlignment(Pos.CENTER);
        valueLabel.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(valueLabel, Priority.ALWAYS);

        card.getChildren().addAll(accentBar, titleLabel, valueLabel);

        return card;
    }

    private void addHoverEffect(Button button, boolean primary) {

        String normalStyle = primary
                ? UITheme.STYLE_PRIMARY_BUTTON
                : UITheme.STYLE_SECONDARY_BUTTON;

        String hoverStyle;

        if (primary) {
            hoverStyle =
                    "-fx-background-color: " + UITheme.SELECTED_BLUE + "; " +
                    "-fx-text-fill: " + UITheme.TEXT_DARK + "; " +
                    "-fx-font-size: " + UITheme.FONT_BODY + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 8; " +
                    "-fx-padding: 8 18 8 18; " +
                    "-fx-border-color: " + UITheme.UW_BLUE + "; " +
                    "-fx-border-width: 1.5; " +
                    "-fx-border-radius: 8;";
        } else {
            hoverStyle =
                    "-fx-background-color: " + UITheme.SELECTED_BLUE + "; " +
                    "-fx-text-fill: " + UITheme.TEXT_DARK + "; " +
                    "-fx-font-size: " + UITheme.FONT_BODY + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 8; " +
                    "-fx-padding: 8 18 8 18; " +
                    "-fx-border-color: " + UITheme.UW_BLUE + "; " +
                    "-fx-border-width: 1.5; " +
                    "-fx-border-radius: 8;";
        }

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }
}