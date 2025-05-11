package ui;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import model.DataManager;
import model.User;

public class TeacherDashboard extends JFrame {
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;
    private User user;
    private Color primaryColor = new Color(25, 100, 200); // Darker blue
    private Color accentColor = new Color(65, 135, 245); // Brighter blue
    private Color buttonHighlightColor = new Color(100, 160, 255); // Light blue for hover
    private Color backgroundColor = new Color(240, 248, 255); // Alice blue
    private Font titleFont = new Font("Arial", Font.BOLD, 16);
    private Font regularFont = new Font("Arial", Font.PLAIN, 14);
    private Font buttonFont = new Font("Arial", Font.BOLD, 14);

    public TeacherDashboard(User user) {
        this.user = user;
        setTitle("Virtual Classroom - Teacher Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        loadData(); // Load data from the database
        setVisible(true);
    }

    private void initializeComponents() {
        // Set up the main layout
        setLayout(new BorderLayout());

        // Create header panel with user info and logout button
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane for main content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(titleFont);
        tabbedPane.setBackground(backgroundColor);

        // Create content panels
        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();

        // Add tabs
        tabbedPane.addTab("Classes", classesPanel);
        tabbedPane.addTab("Quizzes", quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsPanel);

        // Add tabbed pane to main frame
        add(tabbedPane, BorderLayout.CENTER);

        // Add status bar at the bottom
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = createStyledButton("Logout", new Color(220, 53, 69)); // Red color for logout
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // Close this window
                new LoginPage().setVisible(true); // Open login form
            }
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Manage Your Quizzes");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);

        quizListModel = new DefaultListModel<>();
        JList<String> quizList = new JList<>(quizListModel);
        quizList.setFont(regularFont);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton addQuizButton = createStyledButton("Add Quiz", accentColor);
        JButton deleteQuizButton = createStyledButton("Delete Quiz", accentColor);

        buttonPanel.add(addQuizButton);
        buttonPanel.add(deleteQuizButton);

        addQuizButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
            JTextField quizTitle = new JTextField();
            JComboBox<String> classSelector = new JComboBox<>();
            JSpinner dueDateSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));

            classSelector.addItem("Select a class");
            for (int i = 0; i < classListModel.getSize(); i++) {
                String className = classListModel.getElementAt(i).split(" - ")[0];
                classSelector.addItem(className);
            }

            inputPanel.add(new JLabel("Quiz Title:"));
            inputPanel.add(quizTitle);
            inputPanel.add(new JLabel("Select Class:"));
            inputPanel.add(classSelector);
            inputPanel.add(new JLabel("Due in (days):"));
            inputPanel.add(dueDateSpinner);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Add New Quiz", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && !quizTitle.getText().trim().isEmpty()
                    && classSelector.getSelectedIndex() > 0) {

                LocalDate dueDate = LocalDate.now().plusDays((int) dueDateSpinner.getValue());
                boolean success = DataManager.getInstance().addQuizWithDocument(
                        quizTitle.getText().trim(),
                        1, // Replace with actual class ID
                        dueDate,
                        null); // No document for now

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Quiz added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Refresh the UI
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add quiz.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields.",
                        "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteQuizButton.addActionListener(e -> {
            int selectedIndex = quizList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selected = quizListModel.getElementAt(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete: " + selected + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = DataManager.getInstance().deleteQuiz(selected.split(" - Due: ")[0]);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Quiz deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadData(); // Refresh the UI
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete quiz.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a quiz to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createClassesPanel() {
        // Implementation for managing classes
        return new JPanel();
    }

    private JPanel createAssignmentsPanel() {
        // Implementation for managing assignments
        return new JPanel();
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(237, 242, 247));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel statusLabel = new JLabel("Connected to Virtual Classroom Server • " +
                LocalDate.now().toString());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void loadData() {
        DataManager dm = DataManager.getInstance();

        // Load classes
        classListModel.clear();
        dm.getClasses().forEach(classListModel::addElement);

        // Load quizzes
        quizListModel.clear();
        dm.getQuizzes().forEach(quizListModel::addElement);

        // Load assignments
        assignmentListModel.clear();
        dm.getAssignments().forEach(assignmentListModel::addElement);
    }
}