package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AppointmentAnalyticsPage extends JFrame {

    private JTable studentAnalyticsTable;
    private JTable facultyAnalyticsTable;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel facultyTableModel;

    public AppointmentAnalyticsPage() {
        setTitle("Appointment Analytics");
        setSize(1100, 750);
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

        JLabel titleLabel = UIComponents.createTitleLabel("Appointment Analytics");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBackground(UITheme.LIGHT_GREY);

        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBackground(Color.WHITE);
        studentPanel.setBorder(new CompoundBorder(
                new LineBorder(UITheme.UW_BLUE, 2),
                new EmptyBorder(
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5
                )
        ));

        JPanel studentGoldBar = new JPanel();
        studentGoldBar.setBackground(UITheme.UW_GOLD);
        studentGoldBar.setPreferredSize(new Dimension(0, 6));

        JLabel studentLabel = new JLabel("Students With the Most Appointments");
        studentLabel.setFont(UITheme.TITLE_FONT);
        studentLabel.setForeground(UITheme.UW_BLUE);

        JPanel studentTop = new JPanel(new BorderLayout());
        studentTop.setBackground(Color.WHITE);
        studentTop.add(studentGoldBar, BorderLayout.NORTH);
        studentTop.add(studentLabel, BorderLayout.CENTER);

        studentPanel.add(studentTop, BorderLayout.NORTH);

        String[] studentColumns = {"Rank", "Student ID", "Name", "Total Appointments"};
        Object[][] studentData = {
                {"1", "1001", "Ali Khan", "8"},
                {"2", "1002", "Sara Noor", "6"},
                {"3", "1003", "Hassan Malik", "5"}
        };

        studentTableModel = new DefaultTableModel(studentData, studentColumns);
        studentAnalyticsTable = new JTable(studentTableModel);
        styleTable(studentAnalyticsTable);
        studentPanel.add(new JScrollPane(studentAnalyticsTable), BorderLayout.CENTER);

        JPanel facultyPanel = new JPanel(new BorderLayout(10, 10));
        facultyPanel.setBackground(Color.WHITE);
        facultyPanel.setBorder(new CompoundBorder(
                new LineBorder(UITheme.UW_BLUE, 2),
                new EmptyBorder(
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5,
                        UITheme.CARD_PADDING - 5
                )
        ));

        JPanel facultyGoldBar = new JPanel();
        facultyGoldBar.setBackground(UITheme.UW_GOLD);
        facultyGoldBar.setPreferredSize(new Dimension(0, 6));

        JLabel facultyLabel = new JLabel("Faculty With the Most Appointments");
        facultyLabel.setFont(UITheme.TITLE_FONT);
        facultyLabel.setForeground(UITheme.UW_BLUE);

        JPanel facultyTop = new JPanel(new BorderLayout());
        facultyTop.setBackground(Color.WHITE);
        facultyTop.add(facultyGoldBar, BorderLayout.NORTH);
        facultyTop.add(facultyLabel, BorderLayout.CENTER);

        facultyPanel.add(facultyTop, BorderLayout.NORTH);

        String[] facultyColumns = {"Rank", "Faculty ID", "Name", "Total Appointments"};
        Object[][] facultyData = {
                {"1", "F101", "Dr. Ahmed", "12"},
                {"2", "F104", "Dr. Lee", "10"},
                {"3", "F102", "Dr. Khan", "7"}
        };

        facultyTableModel = new DefaultTableModel(facultyData, facultyColumns);
        facultyAnalyticsTable = new JTable(facultyTableModel);
        styleTable(facultyAnalyticsTable);
        facultyPanel.add(new JScrollPane(facultyAnalyticsTable), BorderLayout.CENTER);

        centerPanel.add(studentPanel);
        centerPanel.add(facultyPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        buttonPanel.setBackground(UITheme.LIGHT_GREY);

        JButton refreshButton = UIComponents.createPrimaryButton("Refresh");
        JButton backButton = UIComponents.createSecondaryButton("Back");

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null, "Analytics refreshed.")
        );

        backButton.addActionListener(e -> {
            new ReportsDashboard();
            dispose();
        });

        add(mainPanel);
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(28);
        table.setGridColor(new Color(210, 210, 210));
        table.setSelectionBackground(new Color(220, 230, 245));

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.SUBTITLE_FONT);
        header.setBackground(UITheme.UW_BLUE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
}