package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportsDashboard extends JFrame {

    private static final Color UW_BLUE = new Color(0, 51, 102);
    private static final Color UW_GOLD = new Color(255, 204, 0);
    private static final Color UW_GREY_BG = new Color(240, 242, 245);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    public ReportsDashboard() {
        setTitle("Reports Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UW_GREY_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Reports Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(UW_BLUE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(UW_GREY_BG);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(UW_GREY_BG);

        cardsPanel.add(createSummaryCard("Total Students", "250"));
        cardsPanel.add(createSummaryCard("Total Faculty", "40"));
        cardsPanel.add(createSummaryCard("Total Appointments", "120"));
        cardsPanel.add(createSummaryCard("Most Booked Faculty", "Dr. Ahmed"));

        centerPanel.add(cardsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(UW_GREY_BG);

        JButton studentsButton = createPrimaryButton("View Students Report");
        JButton facultyButton = createPrimaryButton("View Faculty Report");
        JButton analyticsButton = createPrimaryButton("Appointment Analytics");
        JButton backButton = createSecondaryButton("Back");

        buttonPanel.add(studentsButton);
        buttonPanel.add(facultyButton);
        buttonPanel.add(analyticsButton);
        buttonPanel.add(backButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        studentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentsReportPage();
                dispose();
            }
        });

        facultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FacultyReportPage();
                dispose();
            }
        });

        analyticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AppointmentAnalyticsPage();
                dispose();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Back to main menu page.");
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createSummaryCard(String title, String value) {
    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setBackground(Color.WHITE);
    card.setBorder(new CompoundBorder(
            new LineBorder(UW_BLUE, 2),
            new EmptyBorder(12, 16, 12, 16)
    ));

    JPanel accentBar = new JPanel();
    accentBar.setBackground(UW_GOLD);
    accentBar.setPreferredSize(new Dimension(0, 6));

    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    titleLabel.setForeground(UW_BLUE);

    JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
    valueLabel.setForeground(new Color(40, 40, 40));

    JPanel contentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    contentPanel.setBackground(Color.WHITE);
    contentPanel.add(titleLabel);
    contentPanel.add(valueLabel);

    card.add(accentBar, BorderLayout.NORTH);
    card.add(contentPanel, BorderLayout.CENTER);

    return card;
}

  private JButton createPrimaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 25);

    button.setFont(new Font("Segoe UI", Font.BOLD, 15));
    button.setButtonColors(UW_BLUE, new Color(0, 70, 140));
    button.setPreferredSize(new Dimension(220, 44));

    return button;
}

    private JButton createSecondaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 25);

    button.setFont(new Font("Segoe UI", Font.BOLD, 15));
    button.setButtonColors(new Color(130, 130, 130), new Color(100, 100, 100));
    button.setPreferredSize(new Dimension(220, 44));

    return button;
}
}