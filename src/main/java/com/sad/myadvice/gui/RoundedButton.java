package com.sad.myadvice.gui;

import javax.swing.*;
import java.awt.*;

public class UIComponents {

    // Standard radius for buttons
    private static final int BUTTON_RADIUS = 15;

    // Primary button (main actions)
    public static JButton createPrimaryButton(String text) {
        RoundedButton button = new RoundedButton(text, BUTTON_RADIUS);
        button.setFont(UITheme.BODY_FONT);
        button.setButtonColors(UITheme.UW_BLUE, UITheme.UW_GOLD);
        button.setForeground(Color.WHITE);
        return button;
    }

    // Secondary button
    public static JButton createSecondaryButton(String text) {
        RoundedButton button = new RoundedButton(text, BUTTON_RADIUS);
        button.setFont(UITheme.BODY_FONT);
        button.setButtonColors(UITheme.UW_GOLD, UITheme.UW_BLUE);
        button.setForeground(UITheme.TEXT_DARK);
        return button;
    }

    // Page title
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.TITLE_FONT);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    // Subtitle
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.SUBTITLE_FONT);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }
}