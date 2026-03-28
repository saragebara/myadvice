package com.sad.myadvice.adminGUI;

import com.sad.myadvice.adminEntity.ResearchAreas;
import com.sad.myadvice.adminServices.ResearchAreasService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditableResearchAreasScreen {

    private final ResearchAreasService researchAreasService;

    public EditableResearchAreasScreen(ResearchAreasService researchAreasService) {
        this.researchAreasService = researchAreasService;
    }

    public void show(Stage stage) {
        //layout container
        VBox root = new VBox(UITheme.SPACING);
        root.setPadding(new Insets(UITheme.PAGE_PADDING));
        root.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Edit Research Areas");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        //table setup
        TableView<ResearchAreas> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        ObservableList<ResearchAreas> researchAreas =
            FXCollections.observableArrayList(researchAreasService.getAllResearchAreas()); //load data
        table.setItems(researchAreas);

        //user column (read-only)
        TableColumn<ResearchAreas, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cellData -> {
            ResearchAreas ra = cellData.getValue();
            String userName = "No User";
            if (ra != null && ra.getUser() != null) {
                userName = ra.getUser().getName();
            }
            return new SimpleStringProperty(userName);
        });

        //research area column (editable)
        TableColumn<ResearchAreas, String> areaCol = new TableColumn<>("Research Area");
        areaCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getResearchArea()));
        areaCol.setCellFactory(TextFieldTableCell.forTableColumn());
        areaCol.setOnEditCommit(event -> {
            ResearchAreas selected = event.getRowValue();
            selected.setResearchArea(event.getNewValue());
            researchAreasService.updateResearchArea(selected.getId(), selected);
            table.refresh();
        });

        //description column (editable)
        TableColumn<ResearchAreas, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> {
            String desc = cellData.getValue().getDescription();
            return new SimpleStringProperty(desc == null ? "" : desc);
        });
        descCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descCol.setOnEditCommit(event -> {
            ResearchAreas selected = event.getRowValue();
            selected.setDescription(event.getNewValue());
            researchAreasService.updateResearchArea(selected.getId(), selected);
            table.refresh();
        });

        table.getColumns().addAll(userCol, areaCol, descCol);

        //button layout
        HBox buttonBar = new HBox(UITheme.SPACING);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(title, table, buttonBar);

        //set scene
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Admin Research Areas");
        stage.show();
    }
}