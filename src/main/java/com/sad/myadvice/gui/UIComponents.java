package com.sad.myadvice.gui;

import javax.swing.*;
import java.awt.*;

public class UIComponents {

    // Creates a standard primary button used across the GUI
    public static JButton createPrimaryButton(String text) {
        JButton button = new RoundedButton(text);
        button.setBackground(UITheme.UW_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(UITheme.BODY_FONT);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10));
        return button;
    }

    // Creates a secondary button
    public static JButton createSecondaryButton(String text) {
        JButton button = new RoundedButton(text);
        button.setBackground(UITheme.UW_GOLD);
        button.setForeground(UITheme.TEXT_DARK);
        button.setFont(UITheme.BODY_FONT);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10));
        return button;
    }

    // Creates a standard title label
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.TITLE_FONT);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    // Creates a subtitle label
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.SUBTITLE_FONT);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }
}