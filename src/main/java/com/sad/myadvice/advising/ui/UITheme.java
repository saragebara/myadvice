package com.sad.myadvice.advising.ui;

/* SHARED UI THEME FOR JAVAFX (based on Zahra's GUI!!!) */
public class UITheme {

    //COLORS -------------------------------------------------------------------
    public static final String UW_BLUE        = "#003366";
    public static final String UW_GOLD        = "#FFCC00";
    public static final String LIGHT_GREY     = "#F0F2F5";
    public static final String WHITE          = "#FFFFFF";
    public static final String TEXT_DARK      = "#282828";
    public static final String TEXT_LIGHT     = "#CCCCCC";
    public static final String BORDER_GREY    = "#D2D2D2";
    public static final String SOFT_BORDER    = "#D9E1EA";
    public static final String SELECTED_BLUE  = "#DCE6F5";
    public static final String HOVER_BLUE     = "#EAF2FB";
    public static final String HOVER_GOLD     = "#FFE58A";
    public static final String HOVER_CARD_BG = "#EAF2FB"; // soft pastel blue (newly added)

    // FONT SIZES ----------------------------------------------------------------
    public static final int FONT_TITLE    = 24;
    public static final int FONT_SUBTITLE = 17;
    public static final int FONT_BODY     = 14;

    // SPACING/PADDING -----------------------------------------------------------
    public static final int PAGE_PADDING = 28;
    public static final int CARD_PADDING = 20;
    public static final int SPACING      = 14;

    // CSS STYLE STRINGS ---------------------------------------------------------

    public static final String STYLE_SIDEBAR =
        "-fx-background-color: " + UW_BLUE + "; -fx-min-width: 220;";

    public static final String STYLE_SIDEBAR_TITLE =
        "-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";";

    public static final String STYLE_SIDEBAR_SUBTITLE =
        "-fx-font-size: 11; -fx-text-fill: " + TEXT_LIGHT + ";";

    public static final String STYLE_SIDEBAR_BUTTON =
        "-fx-background-color: #1A4D80; " +
        "-fx-text-fill: " + WHITE + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 8; " +
        "-fx-padding: 10 14 10 14;";

    public static final String STYLE_SIDEBAR_BUTTON_ACTIVE =
        "-fx-background-color: " + UW_GOLD + "; " +
        "-fx-text-fill: " + TEXT_DARK + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-font-weight: bold; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 8; " +
        "-fx-padding: 10 14 10 14;";

    public static final String STYLE_PAGE_TITLE =
        "-fx-font-size: " + FONT_TITLE + "; " +
        "-fx-font-weight: bold; " +
        "-fx-text-fill: " + TEXT_DARK + ";";

    public static final String STYLE_SECTION_LABEL =
        "-fx-font-size: " + FONT_SUBTITLE + "; " +
        "-fx-font-weight: bold; " +
        "-fx-text-fill: " + UW_BLUE + ";";

    public static final String STYLE_BODY_LABEL =
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-text-fill: " + TEXT_DARK + ";";

    public static final String STYLE_CARD =
        "-fx-background-color: " + WHITE + "; " +
        "-fx-border-color: " + SOFT_BORDER + "; " +
        "-fx-border-width: 1.5; " +
        "-fx-border-radius: 12; " +
        "-fx-background-radius: 12; " +
        "-fx-padding: " + CARD_PADDING + ";";

    public static final String STYLE_CARD_HOVER =
        "-fx-background-color: " + WHITE + "; " +
        "-fx-border-color: " + UW_BLUE + "; " +
        "-fx-border-width: 1.8; " +
        "-fx-border-radius: 12; " +
        "-fx-background-radius: 12; " +
        "-fx-padding: " + CARD_PADDING + ";";

    public static final String STYLE_GOLD_BAR =
        "-fx-background-color: " + UW_GOLD + "; " +
        "-fx-min-height: 6; -fx-max-height: 6; " +
        "-fx-background-radius: 6;";

    public static final String STYLE_PRIMARY_BUTTON =
        "-fx-background-color: " + UW_BLUE + "; " +
        "-fx-text-fill: " + WHITE + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-font-weight: bold; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 10; " +
        "-fx-padding: 10 20 10 20;";

    public static final String STYLE_PRIMARY_BUTTON_HOVER =
        "-fx-background-color: #0A4A85; " +
        "-fx-text-fill: " + WHITE + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-font-weight: bold; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 10; " +
        "-fx-padding: 10 20 10 20;";

    public static final String STYLE_SECONDARY_BUTTON =
        "-fx-background-color: " + UW_GOLD + "; " +
        "-fx-text-fill: " + TEXT_DARK + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-font-weight: bold; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 10; " +
        "-fx-padding: 10 20 10 20;";

    public static final String STYLE_SECONDARY_BUTTON_HOVER =
        "-fx-background-color: " + HOVER_GOLD + "; " +
        "-fx-text-fill: " + TEXT_DARK + "; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-font-weight: bold; " +
        "-fx-cursor: hand; " +
        "-fx-background-radius: 10; " +
        "-fx-padding: 10 20 10 20;";

    public static final String STYLE_CONTENT_AREA =
        "-fx-background-color: " + LIGHT_GREY + "; " +
        "-fx-padding: " + PAGE_PADDING + ";";

    public static final String STYLE_TEXT_FIELD =
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-border-color: " + BORDER_GREY + "; " +
        "-fx-border-radius: 6; " +
        "-fx-background-radius: 6; " +
        "-fx-padding: 8; " +
        "-fx-background-color: " + WHITE + ";";

    public static final String STYLE_LIST_VIEW =
        "-fx-border-color: " + SOFT_BORDER + "; " +
        "-fx-border-width: 1.5; " +
        "-fx-border-radius: 6; " +
        "-fx-background-radius: 6; " +
        "-fx-font-size: " + FONT_BODY + "; " +
        "-fx-background-color: " + WHITE + ";";

    public static final String STYLE_PROGRESS_BAR =
        "-fx-accent: " + UW_BLUE + ";";

    public static final String STYLE_DETAILS_PANEL =
        "-fx-background-color: " + WHITE + "; " +
        "-fx-border-color: " + SOFT_BORDER + "; " +
        "-fx-border-width: 1.5; " +
        "-fx-border-radius: 8; " +
        "-fx-background-radius: 8; " +
        "-fx-padding: 14;";
}