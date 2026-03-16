package com.sad.myadvice.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class FacultyReportPage extends JFrame {

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

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(UITheme.LIGHT_GREY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING,
                UITheme.PAGE_PADDING
        ));

        JLabel titleLabel = UIComponents.createTitleLabel("Faculty Report");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterWrapper = new JPanel(new BorderLayout(10,10));
        filterWrapper.setBackground(Color.WHITE);
        filterWrapper.setBorder(new CompoundBorder(
                new LineBorder(UITheme.UW_BLUE,2),
                new EmptyBorder(
                        UITheme.CARD_PADDING,
                        UITheme.CARD_PADDING,
                        UITheme.CARD_PADDING,
                        UITheme.CARD_PADDING
                )
        ));

        JPanel goldBar = new JPanel();
        goldBar.setBackground(UITheme.UW_GOLD);
        goldBar.setPreferredSize(new Dimension(0,6));
        filterWrapper.add(goldBar,BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new GridLayout(2,4,15,15));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(createLabel("Faculty ID:"));
        facultyIdField = new JTextField();
        filterPanel.add(facultyIdField);

        filterPanel.add(createLabel("Name:"));
        nameField = new JTextField();
        filterPanel.add(nameField);

        filterPanel.add(createLabel("Department:"));
        departmentComboBox = new JComboBox<>(new String[]{
                "All","Computer Science","Engineering","Mathematics"
        });
        filterPanel.add(departmentComboBox);

        filterPanel.add(createLabel("Availability:"));
        availabilityComboBox = new JComboBox<>(new String[]{
                "All","Available","Busy"
        });
        filterPanel.add(availabilityComboBox);

        filterWrapper.add(filterPanel,BorderLayout.CENTER);
        mainPanel.add(filterWrapper,BorderLayout.NORTH);

        String[] columnNames = {
                "Faculty ID","Name","Department","Email","Appointment Count"
        };

        Object[][] data = {
                {"F101","Dr. Ahmed","Computer Science","ahmed@uwindsor.ca","32"},
                {"F102","Dr. Khan","Engineering","khan@uwindsor.ca","21"},
                {"F103","Dr. Patel","Mathematics","patel@uwindsor.ca","15"},
                {"F104","Dr. Lee","Computer Science","lee@uwindsor.ca","28"}
        };

        tableModel = new DefaultTableModel(data,columnNames);
        facultyTable = new JTable(tableModel);
        styleTable(facultyTable);

        JScrollPane scrollPane = new JScrollPane(facultyTable);
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
                JOptionPane.showMessageDialog(null,"Search will connect to database later.")
        );

        resetButton.addActionListener(e -> {
            facultyIdField.setText("");
            nameField.setText("");
            departmentComboBox.setSelectedIndex(0);
            availabilityComboBox.setSelectedIndex(0);
        });

        generateButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null,"Faculty report generated.")
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