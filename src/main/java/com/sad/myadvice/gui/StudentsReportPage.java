package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StudentsReportPage extends JFrame {

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

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(UITheme.LIGHT_GREY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING
        ));

        JLabel titleLabel = UIComponents.createTitleLabel("Students Report");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterWrapper = new JPanel(new BorderLayout(10,10));
        filterWrapper.setBackground(Color.WHITE);
        filterWrapper.setBorder(new CompoundBorder(
                new LineBorder(UITheme.UW_BLUE,2),
                new EmptyBorder(UITheme.CARD_PADDING,UITheme.CARD_PADDING,UITheme.CARD_PADDING,UITheme.CARD_PADDING)
        ));

        JPanel goldBar = new JPanel();
        goldBar.setBackground(UITheme.UW_GOLD);
        goldBar.setPreferredSize(new Dimension(0,6));
        filterWrapper.add(goldBar,BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new GridLayout(2,4,15,15));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(createLabel("Student ID:"));
        studentIdField = new JTextField();
        filterPanel.add(studentIdField);

        filterPanel.add(createLabel("Name:"));
        nameField = new JTextField();
        filterPanel.add(nameField);

        filterPanel.add(createLabel("Program:"));
        programComboBox = new JComboBox<>(new String[]{
                "All","Computer Science","Software Engineering","Data Science"
        });
        filterPanel.add(programComboBox);

        filterPanel.add(createLabel("Year:"));
        yearComboBox = new JComboBox<>(new String[]{"All","1","2","3","4"});
        filterPanel.add(yearComboBox);

        filterWrapper.add(filterPanel,BorderLayout.CENTER);
        mainPanel.add(filterWrapper,BorderLayout.NORTH);

        String[] columnNames = {
                "Student ID","Name","Program","Year","Email"
        };

        Object[][] data = {
                {"1001","Ali Khan","Computer Science","3","ali@uwindsor.ca"},
                {"1002","Sara Noor","Computer Science","2","sara@uwindsor.ca"},
                {"1003","Hassan Malik","Software Engineering","4","hassan@uwindsor.ca"},
                {"1004","Ayesha Tariq","Data Science","1","ayesha@uwindsor.ca"}
        };

        tableModel = new DefaultTableModel(data,columnNames);
        studentsTable = new JTable(tableModel);
        styleTable(studentsTable);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(new LineBorder(UITheme.UW_BLUE,2));

        mainPanel.add(scrollPane,BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,18,10));
        bottomPanel.setBackground(UITheme.LIGHT_GREY);

        JButton searchButton = UIComponents.createPrimaryButton("Search");
        JButton resetButton = UIComponents.createSecondaryButton("Reset");
        JButton generateButton = UIComponents.createPrimaryButton("Generate Report");
        JButton backButton = UIComponents.createSecondaryButton("Back");

        bottomPanel.add(searchButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(generateButton);
        bottomPanel.add(backButton);

        mainPanel.add(bottomPanel,BorderLayout.SOUTH);

        searchButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null,"Search functionality will be connected later.")
        );

        resetButton.addActionListener(e -> {
            studentIdField.setText("");
            nameField.setText("");
            programComboBox.setSelectedIndex(0);
            yearComboBox.setSelectedIndex(0);
        });

        generateButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null,"Student report generated successfully.")
        );

        backButton.addActionListener(e -> {
            new ReportsDashboard();
            dispose();
        });

        add(mainPanel);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.SUBTITLE_FONT);
        label.setForeground(UITheme.UW_BLUE);
        return label;
    }

    private void styleTable(JTable table) {

        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(28);
        table.setGridColor(new Color(210,210,210));
        table.setSelectionBackground(new Color(220,230,245));

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.SUBTITLE_FONT);
        header.setBackground(UITheme.UW_BLUE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
}