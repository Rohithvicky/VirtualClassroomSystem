package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import model.AttendanceManager;
import model.DataManager;
import model.User;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;
    private User user;

    // Styling variables
    private Color primaryColor = new Color(25, 100, 200); // Darker blue
    private Color accentColor = new Color(65, 135, 245);  // Brighter blue
    private Color buttonHighlightColor = new Color(100, 160, 255); // Light blue for hover
    private Color backgroundColor = new Color(240, 248, 255); // Alice blue
    private Font titleFont = new Font("Arial", Font.BOLD, 16);
    private Font regularFont = new Font("Arial", Font.PLAIN, 14);
    private Font buttonFont = new Font("Arial", Font.BOLD, 14);

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("Student Dashboard - Welcome " + user.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the list models
        classListModel = new DefaultListModel<>();
        quizListModel = new DefaultListModel<>();
        assignmentListModel = new DefaultListModel<>();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        loadData();
        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(titleFont);
        tabbedPane.setBackground(backgroundColor);

        JPanel dashboardPanel = createDashboardPanel();
        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Classes", classesPanel);
        tabbedPane.addTab("Quizzes", quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
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

        JButton logoutButton = createStyledButton("Logout", new Color(220, 53, 69)); // Red for logout
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Your Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(30, 30, 30)); // Dark gray
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 30, 30));
        statsPanel.setBackground(new Color(240, 248, 255)); // Match background
        statsPanel.setBorder(BorderFactory.createEBmptyBorder(30, 30, 30, 30));
B
        // Add stat cards
        statsPanel.add(createStatCard("Classes", String.valueOf(classListModel.size()), new Color(76, 175, 80))); // Green
        statsPanel.add(createStatCard("Quizzes", String.valueOf(quizListModel.size()), new Color(33, 150, 243))); // Blue
        statsPanel.add(createStatCard("Assignments", String.valueOf(assignmentListModel.size()), new Color(255, 152, 0))); // Orange

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JList<String> classList = new JList<>(classListModel);
        classList.setFont(regularFont);

        JButton joinButton = createStyledButton("Join Class", accentColor);
        joinButton.addActionListener(e -> {
            String selected = classList.getSelectedValue();
            if (selected != null) {
                // Use the AttendanceManager class to get the class ID
                int classId = AttendanceManager.getClassIdFromDisplayTitle(selected);

                if (classId == -1) {
                    JOptionPane.showMessageDialog(this, 
                        "Could not find class ID. Please try again or contact support.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Retrieve the Google Meet link for the class
                String meetLink = AttendanceManager.getClassMeetLink(classId);
                if (meetLink != null && !meetLink.isEmpty()) {
                    // Validate the link format
                    if (isValidGoogleMeetLink(meetLink)) {
                        try {
                            // Open the link in the default web browser
                            Desktop.getDesktop().browse(new java.net.URI(meetLink));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, 
                                "Failed to open the class link. Please try again.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "The provided link is not a valid Google Meet link.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No Google Meet link found for the selected class.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
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
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JList<String> quizList = new JList<>(quizListModel);
        quizList.setFont(regularFont);

        JButton takeButton = createStyledButton("Take Quiz", accentColor);
        takeButton.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) {
                int quizId = getQuizIdFromTitle(selected); // Implement this method
                int score = calculateQuizScore(); // Implement this method
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
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JList<String> assignmentList = new JList<>(assignmentListModel);
        assignmentList.setFont(regularFont);

        JButton submitButton = createStyledButton("Submit Assignment", accentColor);
        submitButton.addActionListener(e -> {
            String selected = assignmentList.getSelectedValue();
            if (selected != null) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    int assignmentId = getAssignmentIdFromTitle(selected); // Implement this method
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
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 3), // Border
            BorderFactory.createEmptyBorder(30, 30, 30, 30)   // Padding
        ));
        card.setPreferredSize(new Dimension(200, 200));

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

        // Add shadow effect
        card.setOpaque(true);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(20)); // Spacing
        card.add(valueLabel);

        return card;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(237, 242, 247));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel statusLabel = new JLabel("Connected to Virtual Classroom Server • " + java.time.LocalDate.now());
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

        // Add padding and border
        button.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2), // Darker border
            BorderFactory.createEmptyBorder(10, 20, 10, 20)   // Padding
        ));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonHighlightColor); // Lighter color on hover
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color); // Original color
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Add rounded corners using a custom UI
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw button background
                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);

                // Draw button border
                g2.setColor(color.darker());
                g2.drawRoundRect(0, 0, button.getWidth() - 1, button.getHeight() - 1, 20, 20);

                // Draw button text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(button.getText(), g2).getBounds();
                int textX = (button.getWidth() - stringBounds.width) / 2;
                int textY = (button.getHeight() - stringBounds.height) / 2 + fm.getAscent();
                g2.setColor(button.getForeground());
                g2.drawString(button.getText(), textX, textY);
            }
        });

        return button;
    }

    private void loadData() {
        DataManager dm = DataManager.getInstance();
        classListModel.clear();
        quizListModel.clear();
        assignmentListModel.clear();

        // Load data into the list models
        dm.getClasses().forEach(classListModel::addElement);
        dm.getQuizzes().forEach(quizListModel::addElement);
        dm.getAssignments().forEach(assignmentListModel::addElement);

        // Update the dashboard panel
        updateDashboardPanel();
    }

    private void updateDashboardPanel() {
        // Get the counts for classes, quizzes, and assignments
        int classCount = classListModel.size();
        int quizCount = quizListModel.size();
        int assignmentCount = assignmentListModel.size();

        // Create a new stats panel with updated counts
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(backgroundColor);
        statsPanel.add(createStatCard("Classes", String.valueOf(classCount), Color.GREEN));
        statsPanel.add(createStatCard("Quizzes", String.valueOf(quizCount), Color.BLUE));
        statsPanel.add(createStatCard("Assignments", String.valueOf(assignmentCount), Color.ORANGE));

        // Replace the existing dashboard panel with the updated one
        JPanel dashboardPanel = (JPanel) tabbedPane.getComponentAt(0);
        dashboardPanel.removeAll();
        dashboardPanel.add(new JLabel("Welcome to Student Dashboard", JLabel.CENTER), BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
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

    /**
     * Validates if the given link is a valid Google Meet link.
     * 
     * @param link The link to validate
     * @return true if the link is valid, false otherwise
     */
    private boolean isValidGoogleMeetLink(String link) {
        String googleMeetRegex = "^(https:\\/\\/)?meet\\.google\\.com\\/([a-zA-Z0-9\\-]+)$";
        return link.matches(googleMeetRegex);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = new User(1, "JohnDoe", "student"); // Example user with id, username, and role
            new StudentDashboard(user);
        });
    }
}