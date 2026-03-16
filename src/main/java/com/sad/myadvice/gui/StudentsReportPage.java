package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentsReportPage extends JFrame {

    private static final Color UW_BLUE = new Color(0, 51, 102);
    private static final Color UW_GOLD = new Color(255, 204, 0);
    private static final Color UW_GREY_BG = new Color(240, 242, 245);
    private static final Color PANEL_BG = Color.WHITE;

    private JTextField studentIdField;
    private JTextField nameField;
    private JComboBox<String> programComboBox;
    private JComboBox<String> yearComboBox;
    private JTable studentsTable;
    private DefaultTableModel tableModel;

    public StudentsReportPage() {
        setTitle("Students Report");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UW_GREY_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Students Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(UW_BLUE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterWrapper = new JPanel(new BorderLayout(10, 10));
        filterWrapper.setBackground(PANEL_BG);
        filterWrapper.setBorder(new CompoundBorder(
                new LineBorder(UW_BLUE, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel goldBar = new JPanel();
        goldBar.setBackground(UW_GOLD);
        goldBar.setPreferredSize(new Dimension(0, 6));
        filterWrapper.add(goldBar, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        filterPanel.setBackground(PANEL_BG);

        filterPanel.add(createLabel("Student ID:"));
        studentIdField = new JTextField();
        filterPanel.add(studentIdField);

        filterPanel.add(createLabel("Name:"));
        nameField = new JTextField();
        filterPanel.add(nameField);

        filterPanel.add(createLabel("Program:"));
        programComboBox = new JComboBox<>(new String[]{"All", "Computer Science", "Software Engineering", "Data Science"});
        filterPanel.add(programComboBox);

        filterPanel.add(createLabel("Year:"));
        yearComboBox = new JComboBox<>(new String[]{"All", "1", "2", "3", "4"});
        filterPanel.add(yearComboBox);

        filterWrapper.add(filterPanel, BorderLayout.CENTER);
        mainPanel.add(filterWrapper, BorderLayout.NORTH);

        String[] columnNames = {"Student ID", "Name", "Program", "Year", "Email"};
        Object[][] data = {
                {"1001", "Ali Khan", "Computer Science", "3", "ali@uwindsor.ca"},
                {"1002", "Sara Noor", "Computer Science", "2", "sara@uwindsor.ca"},
                {"1003", "Hassan Malik", "Software Engineering", "4", "hassan@uwindsor.ca"},
                {"1004", "Ayesha Tariq", "Data Science", "1", "ayesha@uwindsor.ca"}
        };

        tableModel = new DefaultTableModel(data, columnNames);
        studentsTable = new JTable(tableModel);
        styleTable(studentsTable);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(new LineBorder(UW_BLUE, 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        bottomPanel.setBackground(UW_GREY_BG);

        JButton searchButton = createPrimaryButton("Search");
        JButton resetButton = createSecondaryButton("Reset");
        JButton generateButton = createPrimaryButton("Generate Report");
        JButton backButton = createSecondaryButton("Back");

        bottomPanel.add(searchButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(generateButton);
        bottomPanel.add(backButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Search functionality will be connected later.");
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentIdField.setText("");
                nameField.setText("");
                programComboBox.setSelectedIndex(0);
                yearComboBox.setSelectedIndex(0);
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Student report generated successfully.");
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

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(UW_BLUE);
        return label;
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