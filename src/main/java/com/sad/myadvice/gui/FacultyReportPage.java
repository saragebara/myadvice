package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FacultyReportPage extends JFrame {

    private static final Color UW_BLUE = new Color(0, 51, 102);
    private static final Color UW_GOLD = new Color(255, 204, 0);
    private static final Color UW_GREY_BG = new Color(240, 242, 245);
    private static final Color PANEL_BG = Color.WHITE;

    private JTextField facultyIdField;
    private JTextField nameField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> availabilityComboBox;
    private JTable facultyTable;
    private DefaultTableModel tableModel;

    public FacultyReportPage() {
        setTitle("Faculty Report");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UW_GREY_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Faculty Report", SwingConstants.CENTER);
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

        filterPanel.add(createLabel("Faculty ID:"));
        facultyIdField = new JTextField();
        filterPanel.add(facultyIdField);

        filterPanel.add(createLabel("Name:"));
        nameField = new JTextField();
        filterPanel.add(nameField);

        filterPanel.add(createLabel("Department:"));
        departmentComboBox = new JComboBox<>(new String[]{
                "All", "Computer Science", "Engineering", "Mathematics"
        });
        filterPanel.add(departmentComboBox);

        filterPanel.add(createLabel("Availability:"));
        availabilityComboBox = new JComboBox<>(new String[]{
                "All", "Available", "Busy"
        });
        filterPanel.add(availabilityComboBox);

        filterWrapper.add(filterPanel, BorderLayout.CENTER);
        mainPanel.add(filterWrapper, BorderLayout.NORTH);

        String[] columnNames = {
                "Faculty ID", "Name", "Department", "Email", "Appointment Count"
        };

        Object[][] data = {
                {"F101", "Dr. Ahmed", "Computer Science", "ahmed@uwindsor.ca", "32"},
                {"F102", "Dr. Khan", "Engineering", "khan@uwindsor.ca", "21"},
                {"F103", "Dr. Patel", "Mathematics", "patel@uwindsor.ca", "15"},
                {"F104", "Dr. Lee", "Computer Science", "lee@uwindsor.ca", "28"}
        };

        tableModel = new DefaultTableModel(data, columnNames);
        facultyTable = new JTable(tableModel);
        styleTable(facultyTable);

        JScrollPane scrollPane = new JScrollPane(facultyTable);
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
                JOptionPane.showMessageDialog(null, "Search will connect to database later.");
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                facultyIdField.setText("");
                nameField.setText("");
                departmentComboBox.setSelectedIndex(0);
                availabilityComboBox.setSelectedIndex(0);
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Faculty report generated.");
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