package com.sad.myadvice.advising.ui.screens;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class FacultyReportScreen {

    // This method builds the Faculty Report page
    // We pass MainController so the Back button can switch back to the dashboard
    public VBox build(User student, MainController controller) {

        // Main page layout
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Page title
        Label title = new Label("Faculty Report");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        // ---------------- FILTER CARD ----------------
        // Card at the top for filtering faculty data
        VBox filterCard = new VBox(UITheme.SPACING);
        filterCard.setStyle(UITheme.STYLE_CARD);

        // Gold accent bar to match the rest of the UI theme
        Region goldBar = new Region();
        goldBar.setStyle(UITheme.STYLE_GOLD_BAR);

        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(15);
        filterGrid.setVgap(15);

        TextField facultyIdField = new TextField();
        TextField nameField = new TextField();

        ComboBox<String> departmentBox = new ComboBox<>(
                FXCollections.observableArrayList("All", "Computer Science", "Engineering", "Mathematics")
        );
        departmentBox.setValue("All");

        ComboBox<String> availabilityBox = new ComboBox<>(
                FXCollections.observableArrayList("All", "Available", "Busy")
        );
        availabilityBox.setValue("All");

        // Add labels and input controls to the filter section
        filterGrid.add(label("Faculty ID:"), 0, 0);
        filterGrid.add(facultyIdField, 1, 0);

        filterGrid.add(label("Name:"), 2, 0);
        filterGrid.add(nameField, 3, 0);

        filterGrid.add(label("Department:"), 0, 1);
        filterGrid.add(departmentBox, 1, 1);

        filterGrid.add(label("Availability:"), 2, 1);
        filterGrid.add(availabilityBox, 3, 1);

        filterCard.getChildren().addAll(goldBar, filterGrid);

        // ---------------- TABLE ----------------
        // Table used to display faculty report data
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Faculty ID", "Name", "Department", "Email", "Appointments"};

        // Dynamically create table columns
        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> col = new TableColumn<>(columns[i]);
            col.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue()[index])
            );
            table.getColumns().add(col);
        }

        // Temporary sample data for layout testing
        table.setItems(FXCollections.observableArrayList(
                new String[]{"F101", "Dr. Ahmed", "Computer Science", "ahmed@uwindsor.ca", "32"},
                new String[]{"F102", "Dr. Khan", "Engineering", "khan@uwindsor.ca", "21"},
                new String[]{"F103", "Dr. Patel", "Mathematics", "patel@uwindsor.ca", "15"},
                new String[]{"F104", "Dr. Lee", "Computer Science", "lee@uwindsor.ca", "28"}
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

        // Temporary placeholder until search is connected to real data
        searchBtn.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION, "Search will connect to database later.").show()
        );

        // Reset all filter values to default
        resetBtn.setOnAction(e -> {
            facultyIdField.clear();
            nameField.clear();
            departmentBox.setValue("All");
            availabilityBox.setValue("All");
        });

        // Temporary placeholder until report generation is fully implemented
        generateBtn.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION, "Faculty report generated.").show()
        );

        // Back button returns to the Reports Dashboard
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