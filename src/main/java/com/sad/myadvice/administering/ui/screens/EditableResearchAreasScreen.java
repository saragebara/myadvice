package com.sad.myadvice.administering.ui.screens;

import com.sad.myadvice.administering.service.ResearchAreasService;
import com.sad.myadvice.advising.ui.UITheme;
import com.sad.myadvice.entity.ResearchAreas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

@Component
public class EditableResearchAreasScreen {

    private final ResearchAreasService researchAreasService;

    public EditableResearchAreasScreen(ResearchAreasService researchAreasService) {
        this.researchAreasService = researchAreasService;
    }

    // Returns VBox to fit inside MainController's contentArea like all other screens
    public VBox build() {
        VBox view = new VBox(UITheme.SPACING);
        view.setStyle(UITheme.STYLE_CONTENT_AREA);

        Label title = new Label("Edit Research Areas");
        title.setStyle(UITheme.STYLE_PAGE_TITLE);

        TableView<ResearchAreas> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(400);

        ObservableList<ResearchAreas> data =
            FXCollections.observableArrayList(researchAreasService.getAllResearchAreas());
        table.setItems(data);

        // User column (read-only) — uses shared User entity
        TableColumn<ResearchAreas, String> userCol = new TableColumn<>("Faculty / Staff");
        userCol.setCellValueFactory(cd -> {
            ResearchAreas ra = cd.getValue();
            String name = (ra != null && ra.getUser() != null)
                ? ra.getUser().getName() : "No User";
            return new SimpleStringProperty(name);
        });

        // Editable research area column — double-click to edit inline
        TableColumn<ResearchAreas, String> areaCol = new TableColumn<>("Research Area");
        areaCol.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getResearchArea()));
        areaCol.setCellFactory(TextFieldTableCell.forTableColumn());
        areaCol.setOnEditCommit(event -> {
            ResearchAreas selected = event.getRowValue();
            selected.setResearchArea(event.getNewValue());
            researchAreasService.updateResearchArea(selected.getId(), selected);
            table.refresh();
        });

        // Editable description column
        TableColumn<ResearchAreas, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cd -> {
            String desc = cd.getValue().getDescription();
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

        Label hint = bodyLabel("Double-click a cell in Research Area or Description to edit it inline.");

        VBox tableCard = new VBox(10, goldBar(), sectionLabel("Faculty Research Areas"), hint, table);
        tableCard.setStyle(UITheme.STYLE_CARD);
        tableCard.setPadding(new Insets(UITheme.CARD_PADDING));

        view.getChildren().addAll(title, tableCard);
        return view;
    }

    private Region goldBar() { Region b = new Region(); b.setStyle(UITheme.STYLE_GOLD_BAR); b.setMaxWidth(Double.MAX_VALUE); return b; }
    private Label sectionLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_SECTION_LABEL); return l; }
    private Label bodyLabel(String t) { Label l = new Label(t); l.setStyle(UITheme.STYLE_BODY_LABEL); return l; }
}