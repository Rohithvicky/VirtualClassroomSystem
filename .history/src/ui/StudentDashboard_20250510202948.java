package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.DataManager;
import model.User;
import virtualclassroom.attendance.AttendanceManager;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;
    private User user;

    // Styling variables
    private Color primaryColor = new Color(25, 100, 200); // Darker blue
    private Color accentColor = new Color(65, 135, 245); // Brighter blue
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
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Reduced font size
        welcomeLabel.setForeground(new Color(30, 30, 30)); // Dark gray
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Adjusted padding
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 8, 8)) { // Reduced gaps
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Add a gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), getWidth(), getHeight(),
                        new Color(220, 235, 250));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduced padding

        // Add stat cards with rounded corners
        statsPanel.add(createRoundedStatCard("Classes", String.valueOf(classListModel.size()), new Color(76, 175, 80))); // Green
        statsPanel.add(createRoundedStatCard("Quizzes", String.valueOf(quizListModel.size()), new Color(33, 150, 243))); // Blue
        statsPanel.add(createRoundedStatCard("Assignments", String.valueOf(assignmentListModel.size()),
                new Color(255, 152, 0))); // Orange

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoundedStatCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10)); // Spacing
        card.add(valueLabel);

        return card;
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
                    JOptionPane.showMessageDialog(this, "Failed to record quiz attempt.", "Error",
                            JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(this, "Failed to submit assignment.", "Error",
                                JOptionPane.ERROR_MESSAGE);
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
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Paint text
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();
                g2.setColor(getForeground());
                g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonHighlightColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 8, 8)) { // Reduced gaps
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Add a gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), getWidth(), getHeight(),
                        new Color(220, 235, 250));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduced padding

        // Add smaller stat cards with rounded corners
        statsPanel.add(createSmallRoundedStatCard("Classes", String.valueOf(classCount), new Color(76, 175, 80))); // Green
        statsPanel.add(createSmallRoundedStatCard("Quizzes", String.valueOf(quizCount), new Color(33, 150, 243))); // Blue
        statsPanel.add(
                createSmallRoundedStatCard("Assignments", String.valueOf(assignmentCount), new Color(255, 152, 0))); // Orange

        // Replace the existing dashboard panel with the updated one
        JPanel dashboardPanel = (JPanel) tabbedPane.getComponentAt(0);
        dashboardPanel.removeAll();

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Your Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Reduced font size
        welcomeLabel.setForeground(new Color(30, 30, 30)); // Dark gray
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Adjusted padding
        dashboardPanel.add(welcomeLabel, BorderLayout.NORTH);

        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    private JPanel createSmallRoundedStatCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8); // Smaller corner radius

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Reduced padding

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Smaller font size
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Smaller font size for value
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5)); // Reduced spacing
        card.add(valueLabel);

        return card;
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

}