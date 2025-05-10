package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the list models
        classListModel = new DefaultListModel<>();
        quizListModel = new DefaultListModel<>();
        assignmentListModel = new DefaultListModel<>();

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        // Main layout with a border to create margins
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Tabbed pane with custom styling
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(52, 73, 94)); // Dark blue-gray

        // Create panels for each tab
        JPanel dashboardPanel = createDashboardPanel();
        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Classes", classesPanel);
        tabbedPane.addTab("Quizzes", quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Your Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(30, 30, 30)); // Dark gray
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 30, 30));
        statsPanel.setBackground(new Color(240, 248, 255)); // Match background
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Add stat cards
        statsPanel.add(createStatCard("Classes", "0", new Color(76, 175, 80))); // Green
        statsPanel.add(createStatCard("Quizzes", "0", new Color(33, 150, 243))); // Blue
        statsPanel.add(createStatCard("Assignments", "0", new Color(255, 152, 0))); // Orange

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 3), // Border
            BorderFactory.createEmptyBorder(30, 30, 30, 30)   // Padding
        ));

        // Title Label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Value Label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(20)); // Spacing
        card.add(valueLabel);

        return card;
    }

    private JPanel createClassesPanel() {
        return createSimpleTabPanel("Classes");
    }

    private JPanel createQuizzesPanel() {
        return createSimpleTabPanel("Quizzes");
    }

    private JPanel createAssignmentsPanel() {
        return createSimpleTabPanel("Assignments");
    }

    private JPanel createSimpleTabPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title + " will be displayed here.", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(100, 100, 100)); // Gray
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentDashboard::new);
    }
}