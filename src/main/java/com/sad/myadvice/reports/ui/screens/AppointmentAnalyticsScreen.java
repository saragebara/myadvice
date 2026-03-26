package com.sad.myadvice.reports.ui.screens;

import com.sad.myadvice.advising.ui.MainController;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.reports.service.ReportsService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class AppointmentAnalyticsScreen {
	//backend service
	private final ReportsService reportsService;
	public AppointmentAnalyticsScreen(ReportsService reportsService) {
		this.reportsService = reportsService;
	}

    // This method builds the Appointment Analytics page
    // MainController is passed in so the Back button can switch screens
    public VBox build(User student, MainController controller) {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        // Page title
        Label titleLabel = new Label("Appointment Analytics");
        titleLabel.setStyle(UITheme.STYLE_PAGE_TITLE);

        //Backend - getting the top students based on # of appts
        List<Map.Entry<User, Integer>> topStudents =
			reportsService.getTopStudentsByAppointments(3); //limiting to top 3 students
		String[][] studentData = new String[topStudents.size()][4];
		for (int i = 0; i < topStudents.size(); i++) {
			User s = topStudents.get(i).getKey();
			studentData[i] = new String[]{
				String.valueOf(i + 1),
				s.getStudentId() != null ? s.getStudentId() : "N/A",
				s.getName(),
				String.valueOf(topStudents.get(i).getValue())
			};
		}
		// Card showing students with the highest number of appointments
		VBox studentCard = createAnalyticsCard(
			"Students With the Most Appointments",
			new String[]{"Rank", "Student ID", "Name", "Total Appointments"},
			studentData.length > 0 ? studentData : new String[][]{{"—", "—", "No data yet", "0"}}
		);


        //Backend - getting the top faculty based on # of appts
        List<Map.Entry<User, Integer>> topFaculty =
			reportsService.getTopFacultyByAppointments(3);
		String[][] facultyData = new String[topFaculty.size()][4];
		for (int i = 0; i < topFaculty.size(); i++) {
			User f = topFaculty.get(i).getKey();
			facultyData[i] = new String[]{
				String.valueOf(i + 1),
				f.getStudentId() != null ? f.getStudentId() : "N/A",
				f.getName(),
				String.valueOf(topFaculty.get(i).getValue())
			};
		}
		// Card showing faculty with the highest number of appointments
		VBox facultyCard = createAnalyticsCard(
			"Faculty With the Most Appointments",
			new String[]{"Rank", "Faculty ID", "Name", "Total Appointments"},
			facultyData.length > 0 ? facultyData : new String[][]{{"—", "—", "No data yet", "0"}}
		);

        // Buttons at the bottom of the page
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle(UITheme.STYLE_PRIMARY_BUTTON);

        Button backButton = new Button("Back");
        backButton.setStyle(UITheme.STYLE_SECONDARY_BUTTON);

        // Back button returns to the Reports Dashboard
        backButton.setOnAction(e -> controller.showReportsDashboard());

        HBox buttonBar = new HBox(18, refreshButton, backButton);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

		//working refresh logic
        refreshButton.setOnAction(e -> {
			view.getChildren().clear();
			view.getChildren().addAll(
				titleLabel,
				reportsService.getTopStudentsByAppointments(3).isEmpty()
					? studentCard : studentCard,
				facultyCard,
				buttonBar
			);
		});
		
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