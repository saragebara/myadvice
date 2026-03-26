package com.sad.myadvice.reports.ui.screens;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class StudentsReportScreen {

    // This method builds the Students Report page
    // We pass MainController so the Back button can switch screens
    public VBox build(User student, MainController controller) {

        // Main page layout
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Page title
        Label title = new Label("Students Report");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        // ---------------- FILTER CARD ----------------
        // Card at the top for filtering/searching student info
        VBox filterCard = new VBox(UITheme.SPACING);
        filterCard.setStyle(UITheme.STYLE_CARD);

        // Gold accent bar to match the rest of the app theme
        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);

        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(15);
        filterGrid.setVgap(15);

        TextField studentIdField = new TextField();
        TextField nameField = new TextField();

        ComboBox<String> programBox = new ComboBox<>(
                FXCollections.observableArrayList("All", "Computer Science", "Software Engineering", "Data Science")
        );
        programBox.setValue("All");

        ComboBox<String> yearBox = new ComboBox<>(
                FXCollections.observableArrayList("All", "1", "2", "3", "4")
        );
        yearBox.setValue("All");

        // Add labels and input fields to filter grid
        filterGrid.add(label("Student ID:"), 0, 0);
        filterGrid.add(studentIdField, 1, 0);

        filterGrid.add(label("Name:"), 2, 0);
        filterGrid.add(nameField, 3, 0);

        filterGrid.add(label("Program:"), 0, 1);
        filterGrid.add(programBox, 1, 1);

        filterGrid.add(label("Year:"), 2, 1);
        filterGrid.add(yearBox, 3, 1);

        filterCard.getChildren().addAll(goldBar, filterGrid);

        // ---------------- TABLE ----------------
        // Table to display student report data
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Student ID", "Name", "Program", "Year", "Email"};

        // Dynamically creating table columns
        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> col = new TableColumn<>(columns[i]);
            col.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue()[index])
            );
            table.getColumns().add(col);
        }

        // Temporary sample data for UI testing
        table.setItems(FXCollections.observableArrayList(
                new String[]{"1001", "Ali Khan", "Computer Science", "3", "ali@uwindsor.ca"},
                new String[]{"1002", "Sara Noor", "Computer Science", "2", "sara@uwindsor.ca"},
                new String[]{"1003", "Hassan Malik", "Software Engineering", "4", "hassan@uwindsor.ca"},
                new String[]{"1004", "Ayesha Tariq", "Data Science", "1", "ayesha@uwindsor.ca"}
        ));

        // ---------------- BUTTONS ----------------
        Button searchBtn = new Button("Search");
        searchBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button backBtn = new Button("Back");
        backBtn.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        // Temporary placeholder for search logic
        searchBtn.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION, "Search functionality will be connected later.").show()
        );

        // Reset all filter fields back to default
        resetBtn.setOnAction(e -> {
            studentIdField.clear();
            nameField.clear();
            programBox.setValue("All");
            yearBox.setValue("All");
        });

        // Temporary placeholder for generate report logic
        generateBtn.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION, "Student report generated successfully.").show()
        );

        // Back button returns user to the Reports Dashboard
        backBtn.setOnAction(e -> controller.showReportsDashboard());

        HBox buttonBar = new HBox(18, searchBtn, resetBtn, generateBtn, backBtn);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        // ---------------- FINAL LAYOUT ----------------
        view.getChildren().addAll(title, filterCard, table, buttonBar);

        return view;
    }

    // Helper method for styled labels in the filter section
    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle(UITheme.STYLE_SECTION_LABEL);
        return l;
    }
}