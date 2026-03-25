package com.sad.myadvice.gui;

import javax.swing.*;
import java.awt.*;

public class UIComponents {

    // Creates a standard primary button used across the GUI
    public static JButton createPrimaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 15);
    button.setFont(UITheme.BODY_FONT);
    button.setButtonColors(UITheme.UW_BLUE, UITheme.UW_BLUE.brighter());
    button.setForeground(Color.WHITE);
    return button;
    }

    // Creates a secondary button
   public static JButton createSecondaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 15);
    button.setFont(UITheme.BODY_FONT);
    button.setButtonColors(new Color(120,120,120), new Color(90,90,90));
    button.setForeground(Color.WHITE);
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