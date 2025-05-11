package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import model.DataManager;
import model.Quiz;
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
    private Color backgroundColor = new Color(240, 248, 255); // Alice blue
    private Font titleFont = new Font("Arial", Font.BOLD, 16);
    private Font regularFont = new Font("Arial", Font.PLAIN, 14);
    private Font buttonFont = new Font("Arial", Font.BOLD, 14);
    private Font largeFont = new Font("Arial", Font.BOLD, 24);

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("Student Dashboard - Welcome " + user.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Use a fallback icon if resource is not found
        try {
            setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
        } catch (Exception ex) {
            System.err.println("Icon not found, continuing without custom icon.");
        }

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

        // Use a fallback icon if resources are missing.
        ImageIcon dashIcon = getSafeIcon("/icons/dashboard.png");
        ImageIcon classesIcon = getSafeIcon("/icons/classes.png");
        ImageIcon quizIcon = getSafeIcon("/icons/quiz.png");
        ImageIcon assignmentIcon = getSafeIcon("/icons/assignment.png");

        tabbedPane.addTab("Dashboard", dashIcon, dashboardPanel);
        tabbedPane.addTab("Classes", classesIcon, classesPanel);
        tabbedPane.addTab("Quizzes", quizIcon, quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentIcon, assignmentsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private ImageIcon getSafeIcon(String path) {
        try {
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception ex) {
            System.err.println("Icon not found for path " + path);
            return new ImageIcon();
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left panel for avatar and welcome message
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(primaryColor);
        ImageIcon avatarIcon;
        try {
            avatarIcon = new ImageIcon(getClass().getResource("/icons/avatar.png"));
        } catch (Exception ex) {
            System.err.println("Avatar icon not found.");
            avatarIcon = new ImageIcon();
        }
        JLabel avatarLabel = new JLabel(avatarIcon);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(avatarLabel);
        leftPanel.add(welcomeLabel);
        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right side - Logout button using our styled button method
        JButton logoutButton = createStyledButton("Logout", new Color(220, 53, 69));
        try {
            logoutButton.setIcon(new ImageIcon(getClass().getResource("/icons/logout.png")));
        } catch (Exception ex) {
            System.err.println("Logout icon missing.");
        }
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(backgroundColor);
        JLabel welcomeLabel = new JLabel("Your Learning Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(largeFont);
        welcomeLabel.setForeground(primaryColor);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(welcomePanel, BorderLayout.NORTH);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        statsPanel.setBackground(backgroundColor);
        statsPanel.add(
                createDashboardCard("Classes", classListModel.size(), new Color(70, 130, 180), "/icons/class.png"));
        statsPanel
                .add(createDashboardCard("Quizzes", quizListModel.size(), new Color(60, 179, 113), "/icons/quiz.png"));
        statsPanel.add(createDashboardCard("Assignments", assignmentListModel.size(), new Color(218, 165, 32),
                "/icons/assignment.png"));
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDashboardCard(String title, int count, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setPreferredSize(new Dimension(200, 150));

        // Top panel with icon and title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(color);
        JLabel iconLabel = new JLabel(getSafeIcon(iconPath));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(iconLabel);
        topPanel.add(titleLabel);
        card.add(topPanel, BorderLayout.NORTH);

        // Center panel with count
        JLabel countLabel = new JLabel(String.valueOf(count), JLabel.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 36));
        countLabel.setForeground(Color.WHITE);
        card.add(countLabel, BorderLayout.CENTER);

        // Bottom panel with "View All" button
        JButton viewButton = createStyledButton("View All", new Color(30, 30, 30));
        viewButton.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(color);
        buttonPanel.add(viewButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Action to switch tabs according to title
        viewButton.addActionListener(e -> {
            switch (title) {
                case "Classes":
                    tabbedPane.setSelectedIndex(1);
                    break;
                case "Quizzes":
                    tabbedPane.setSelectedIndex(2);
                    break;
                case "Assignments":
                    tabbedPane.setSelectedIndex(3);
                    break;
            }
        });

        return card;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("Your Classes");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Class list
        JList<String> classList = new JList<>(classListModel);
        classList.setFont(regularFont);
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.setCellRenderer(new CustomListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(classList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        JButton refreshButton = createStyledButton("Refresh", new Color(30, 30, 30));
        refreshButton.setIcon(getSafeIcon("/icons/refresh.png"));
        refreshButton.addActionListener(e -> loadData());
        JButton joinButton = createStyledButton("Join Class", new Color(30, 30, 30));
        joinButton.setIcon(getSafeIcon("/icons/join.png"));
        joinButton.addActionListener(e -> {
            String selected = classList.getSelectedValue();
            if (selected != null) {
                joinClass(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a class first",
                        "No Class Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(refreshButton);
        buttonPanel.add(joinButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void joinClass(String className) {
        try {
            int classId = AttendanceManager.getClassIdFromDisplayTitle(className);
            if (classId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Could not find class details. Please try again later.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String meetLink = AttendanceManager.getClassMeetLink(classId);
            if (meetLink == null || meetLink.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No meeting link available for this class.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidGoogleMeetLink(meetLink)) {
                JOptionPane.showMessageDialog(this,
                        "Invalid meeting link format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean attendanceRecorded = AttendanceManager.recordAttendance(
                    classId, user.getId(), LocalDate.now().toString());
            if (!attendanceRecorded) {
                JOptionPane.showMessageDialog(this,
                        "Could not record attendance automatically. Please inform your instructor.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(meetLink));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not open browser automatically. Please visit: " + meetLink,
                        "Join Class", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error joining class: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("Available Quizzes");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Quiz list
        JList<String> quizList = new JList<>(quizListModel);
        quizList.setFont(regularFont);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setCellRenderer(new QuizListRenderer());
        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        JButton refreshButton = createStyledButton("Refresh", new Color(30, 30, 30));
        refreshButton.setIcon(getSafeIcon("/icons/refresh.png"));
        refreshButton.addActionListener(e -> loadData());
        JButton takeButton = createStyledButton("Take Quiz", new Color(30, 30, 30));
        takeButton.setIcon(getSafeIcon("/icons/start.png"));
        takeButton.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) {
                takeQuiz(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a quiz first",
                        "No Quiz Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(refreshButton);
        buttonPanel.add(takeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("Your Assignments");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Assignment list
        JList<String> assignmentList = new JList<>(assignmentListModel);
        assignmentList.setFont(regularFont);
        assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentList.setCellRenderer(new AssignmentListRenderer());
        JScrollPane scrollPane = new JScrollPane(assignmentList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        JButton refreshButton = createStyledButton("Refresh", new Color(30, 30, 30));
        refreshButton.setIcon(getSafeIcon("/icons/refresh.png"));
        refreshButton.addActionListener(e -> loadData());
        JButton submitButton = createStyledButton("Submit", new Color(30, 30, 30));
        submitButton.setIcon(getSafeIcon("/icons/upload.png"));
        submitButton.addActionListener(e -> {
            String selected = assignmentList.getSelectedValue();
            if (selected != null) {
                submitAssignment(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select an assignment first",
                        "No Assignment Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(refreshButton);
        buttonPanel.add(submitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void submitAssignment(String assignmentTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Submit");
        fileChooser.setApproveButtonText("Submit");
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            int assignmentId = DataManager.getInstance().getAssignmentsObj().stream()
                    .filter(a -> a.getTitle().equals(assignmentTitle))
                    .findFirst()
                    .map(a -> a.getId())
                    .orElse(-1);
            if (assignmentId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Could not identify assignment. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean success = DataManager.getInstance().submitAssignment(
                    assignmentId, user.getId(), selectedFile.getAbsolutePath());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Assignment '" + assignmentTitle + "' submitted successfully!",
                        "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to submit assignment. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        DataManager dm = DataManager.getInstance();
        classListModel.clear();
        quizListModel.clear();
        assignmentListModel.clear();

        // Load classes
        dm.getClasses().forEach(classListModel::addElement);
        // Load quizzes (if quiz string does not already include due date, append
        // current date)
        dm.getQuizzesObj().forEach(quiz -> quizListModel.addElement(quiz.getTitle()));
        // Load assignments
        dm.getAssignmentsObj().forEach(assignment -> assignmentListModel.addElement(assignment.getTitle()));

        updateDashboardStats();
    }

    private void updateDashboardStats() {
        repaint();
    }

    private void takeQuiz(String quizTitle) {
        Quiz quiz = DataManager.getInstance().getQuiz(quizTitle);
        if (quiz != null) {
            List<String[]> questions = DataManager.getInstance().getQuizQuestions(quizTitle);
            if (questions == null || questions.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No questions found for this quiz.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JDialog quizDialog = new JDialog(this, "Quiz: " + quizTitle, true);
            quizDialog.setSize(800, 600);
            quizDialog.setLocationRelativeTo(this);
            JPanel quizPanel = new JPanel(new BorderLayout());
            quizPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            timerPanel.setBackground(Color.WHITE);
            JLabel timerLabel = new JLabel("Time remaining: 30:00");
            timerLabel.setFont(buttonFont);
            timerLabel.setForeground(primaryColor);
            timerPanel.add(timerLabel);
            quizPanel.add(timerPanel, BorderLayout.NORTH);

            JPanel questionsPanel = new JPanel();
            questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
            questionsPanel.setBackground(Color.WHITE);
            ButtonGroup[] buttonGroups = new ButtonGroup[questions.size()];
            JRadioButton[][] optionButtons = new JRadioButton[questions.size()][4];
            for (int i = 0; i < questions.size(); i++) {
                String[] questionData = questions.get(i);
                JPanel questionPanel = new JPanel(new BorderLayout());
                questionPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Question " + (i + 1)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                questionPanel.setBackground(Color.WHITE);
                JLabel questionLabel = new JLabel("<html>" + questionData[0] + "</html>");
                questionLabel.setFont(regularFont);
                questionPanel.add(questionLabel, BorderLayout.NORTH);
                JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
                optionsPanel.setBackground(Color.WHITE);
                buttonGroups[i] = new ButtonGroup();
                for (int j = 0; j < 4; j++) {
                    optionButtons[i][j] = new JRadioButton("<html>" + questionData[j + 1] + "</html>");
                    optionButtons[i][j].setFont(regularFont);
                    optionButtons[i][j].setBackground(Color.WHITE);
                    buttonGroups[i].add(optionButtons[i][j]);
                    optionsPanel.add(optionButtons[i][j]);
                }
                questionPanel.add(optionsPanel, BorderLayout.CENTER);
                questionsPanel.add(questionPanel);
                questionsPanel.add(Box.createVerticalStrut(10));
            }
            JScrollPane scrollPane = new JScrollPane(questionsPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            quizPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            buttonPanel.setBackground(Color.WHITE);
            JButton submitButton = createStyledButton("Submit Quiz", new Color(30, 30, 30));
            submitButton.addActionListener(e -> {
                int score = 0;
                for (int i = 0; i < questions.size(); i++) {
                    String correctOption = questions.get(i)[5];
                    for (int j = 0; j < 4; j++) {
                        if (optionButtons[i][j].isSelected() &&
                                correctOption.equalsIgnoreCase(String.valueOf((char) ('A' + j)))) {
                            score++;
                        }
                    }
                }
                quizDialog.dispose();
                boolean resultRecorded = DataManager.getInstance().recordQuizResult(
                        user.getId(), quiz.getId(), score, questions.size());
                String message = "You scored " + score + " out of " + questions.size() + "!";
                if (!resultRecorded) {
                    message += "\n(Result could not be saved automatically. Please inform your instructor.)";
                }
                JOptionPane.showMessageDialog(this, message, "Quiz Completed", JOptionPane.INFORMATION_MESSAGE);
            });
            buttonPanel.add(submitButton);
            quizPanel.add(buttonPanel, BorderLayout.SOUTH);
            quizDialog.add(quizPanel);
            quizDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Quiz not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom button style similar to TeacherDashboard (rounded with extra padding)
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);
                g2.setColor(color.darker());
                g2.drawRoundRect(0, 0, button.getWidth() - 1, button.getHeight() - 1, 20, 20);
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

    // Simple method to validate a Google Meet link format
    private boolean isValidGoogleMeetLink(String link) {
        return link != null && link.matches("^(https?:\\/\\/)?meet\\.google\\.com\\/[a-zA-Z0-9\\-]+$");
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(237, 242, 247));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        JLabel statusLabel = new JLabel("Connected to Virtual Classroom Server • " + LocalDate.now().toString());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    // Custom list renderers
    private class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(accentColor);
                label.setForeground(Color.WHITE);
            } else if (index % 2 == 0) {
                label.setBackground(new Color(240, 248, 255));
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }

    // Renderer for quiz list; similar to custom list renderer
    private class QuizListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(accentColor);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }

    // Renderer for assignment list; similar to quiz renderer
    private class AssignmentListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(accentColor);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }
}