package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ReportsDashboard extends JFrame {

    public ReportsDashboard() {
        setTitle("Reports Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UITheme.LIGHT_GREY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING
        ));

        JLabel titleLabel = UIComponents.createTitleLabel("Reports Dashboard");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(UITheme.LIGHT_GREY);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(UITheme.LIGHT_GREY);

        cardsPanel.add(createSummaryCard("Total Students", "250"));
        cardsPanel.add(createSummaryCard("Total Faculty", "40"));
        cardsPanel.add(createSummaryCard("Total Appointments", "120"));
        cardsPanel.add(createSummaryCard("Most Booked Faculty", "Dr. Ahmed"));

        centerPanel.add(cardsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(UITheme.LIGHT_GREY);

        JButton studentsButton = UIComponents.createPrimaryButton("View Students Report");
        JButton facultyButton = UIComponents.createPrimaryButton("View Faculty Report");
        JButton analyticsButton = UIComponents.createPrimaryButton("Appointment Analytics");
        JButton backButton = UIComponents.createSecondaryButton("Back");

        buttonPanel.add(studentsButton);
        buttonPanel.add(facultyButton);
        buttonPanel.add(analyticsButton);
        buttonPanel.add(backButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        studentsButton.addActionListener(e -> {
            new StudentsReportPage();
            dispose();
        });

        facultyButton.addActionListener(e -> {
            new FacultyReportPage();
            dispose();
        });

        analyticsButton.addActionListener(e -> {
            new AppointmentAnalyticsPage();
            dispose();
        });

        backButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null, "Back to main menu page.")
        );

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createSummaryCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UITheme.UW_BLUE, 2),
                new EmptyBorder(
                        UITheme.CARD_PADDING - 3,
                        UITheme.CARD_PADDING + 1,
                        UITheme.CARD_PADDING - 3,
                        UITheme.CARD_PADDING + 1
                )
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(UITheme.UW_GOLD);
        accentBar.setPreferredSize(new Dimension(0, 6));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UITheme.SUBTITLE_FONT);
        titleLabel.setForeground(UITheme.UW_BLUE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }
}