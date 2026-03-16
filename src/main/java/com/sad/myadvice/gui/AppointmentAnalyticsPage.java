package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppointmentAnalyticsPage extends JFrame {

    private static final Color UW_BLUE = new Color(0, 51, 102);
    private static final Color UW_GOLD = new Color(255, 204, 0);
    private static final Color UW_GREY_BG = new Color(240, 242, 245);
    private static final Color PANEL_BG = Color.WHITE;

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
        mainPanel.setBackground(UW_GREY_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Appointment Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(UW_BLUE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBackground(UW_GREY_BG);

        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBackground(PANEL_BG);
        studentPanel.setBorder(new CompoundBorder(
                new LineBorder(UW_BLUE, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel studentGoldBar = new JPanel();
        studentGoldBar.setBackground(UW_GOLD);
        studentGoldBar.setPreferredSize(new Dimension(0, 6));

        JLabel studentLabel = new JLabel("Students With the Most Appointments");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        studentLabel.setForeground(UW_BLUE);

        JPanel studentTop = new JPanel(new BorderLayout());
        studentTop.setBackground(PANEL_BG);
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
        facultyPanel.setBackground(PANEL_BG);
        facultyPanel.setBorder(new CompoundBorder(
                new LineBorder(UW_BLUE, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel facultyGoldBar = new JPanel();
        facultyGoldBar.setBackground(UW_GOLD);
        facultyGoldBar.setPreferredSize(new Dimension(0, 6));

        JLabel facultyLabel = new JLabel("Faculty With the Most Appointments");
        facultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        facultyLabel.setForeground(UW_BLUE);

        JPanel facultyTop = new JPanel(new BorderLayout());
        facultyTop.setBackground(PANEL_BG);
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
        buttonPanel.setBackground(UW_GREY_BG);

        JButton refreshButton = createPrimaryButton("Refresh");
        JButton backButton = createSecondaryButton("Back");

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Analytics refreshed.");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReportsDashboard();
                dispose();
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(210, 210, 210));
        table.setSelectionBackground(new Color(220, 230, 245));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(UW_BLUE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

   private JButton createPrimaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 25);

    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setButtonColors(UW_BLUE, new Color(0, 70, 140));
    button.setPreferredSize(new Dimension(170, 42));

    return button;
}

    private JButton createSecondaryButton(String text) {
    RoundedButton button = new RoundedButton(text, 25);

    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setButtonColors(new Color(130, 130, 130), new Color(100, 100, 100));
    button.setPreferredSize(new Dimension(170, 42));

    return button;
}
}