package ui;

import java.awt.*;
import javax.swing.*;
import model.DataManager;
import model.User;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;
    private User user;

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("Student Dashboard - Welcome " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the list models
        classListModel = new DefaultListModel<>();
        quizListModel = new DefaultListModel<>();
        assignmentListModel = new DefaultListModel<>();

        initializeComponents();
        loadData();
        setVisible(true);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        JPanel dashboardPanel = createDashboardPanel();
        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();
        
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Classes", classesPanel);
        tabbedPane.addTab("Quizzes", quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        createMenuBar();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome to Student Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.add(createStatCard("Classes", String.valueOf(classListModel.size()), Color.GREEN));
        statsPanel.add(createStatCard("Quizzes", String.valueOf(quizListModel.size()), Color.BLUE));
        statsPanel.add(createStatCard("Assignments", String.valueOf(assignmentListModel.size()), Color.ORANGE));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JList<String> classList = new JList<>(classListModel);
        
        JButton joinButton = new JButton("Join Class");
        joinButton.addActionListener(e -> {
            String selected = classList.getSelectedValue();
            if (selected != null) {
                int classId = getClassIdFromTitle(selected); // Implement this method to map title to ID
                boolean success = DataManager.getInstance().attendClass(classId, user.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Attendance recorded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record attendance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a class first");
            }
        });
        
        panel.add(new JScrollPane(classList), BorderLayout.CENTER);
        panel.add(joinButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JList<String> quizList = new JList<>(quizListModel);
        
        JButton takeButton = new JButton("Take Quiz");
        takeButton.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) {
                int quizId = getQuizIdFromTitle(selected); // Implement this method to map title to ID
                int score = calculateQuizScore(); // Implement this method to calculate the score
                boolean success = DataManager.getInstance().takeQuiz(quizId, user.getId(), score);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Quiz attempt recorded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record quiz attempt.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a quiz first");
            }
        });
        
        panel.add(new JScrollPane(quizList), BorderLayout.CENTER);
        panel.add(takeButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JList<String> assignmentList = new JList<>(assignmentListModel);
        
        JButton submitButton = new JButton("Submit Assignment");
        submitButton.addActionListener(e -> {
            String selected = assignmentList.getSelectedValue();
            if (selected != null) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    int assignmentId = getAssignmentIdFromTitle(selected); // Implement this method to map title to ID
                    boolean success = DataManager.getInstance().submitAssignment(assignmentId, user.getId(), filePath);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Assignment submitted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to submit assignment.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an assignment first");
            }
        });
        
        panel.add(new JScrollPane(assignmentList), BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> refreshData());
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            this.dispose();
            new LoginPage();
        });
        
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void loadData() {
        DataManager dm = DataManager.getInstance();
        dm.getClasses().forEach(classListModel::addElement);
        dm.getQuizzes().forEach(quizListModel::addElement);
        dm.getAssignments().forEach(assignmentListModel::addElement);
    }

    private void refreshData() {
        classListModel.clear();
        quizListModel.clear();
        assignmentListModel.clear();
        loadData();
    }

    private int getAssignmentIdFromTitle(String title) {
        // Implement this method to map assignment title to its ID
        return 0; // Placeholder implementation
    }

    private int getClassIdFromTitle(String title) {
        // Implement this method to map class title to its ID
        return 0; // Placeholder implementation
    }

    private int getQuizIdFromTitle(String title) {
        // Implement this method to map quiz title to its ID
        return 0; // Placeholder implementation
    }

    private int calculateQuizScore() {
        // Implement this method to calculate the quiz score
        return 0; // Placeholder implementation
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = new User(1, "JohnDoe", "student"); // Example user with id, username, and role
            new StudentDashboard(user);
        });
    }
}