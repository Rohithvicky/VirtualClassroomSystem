package ui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.AttendanceManager;
import model.DataManager;
import model.User;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> classListModel;
    private DefaultListModel<String> quizListModel;
    private DefaultListModel<String> assignmentListModel;
    private User user;
    private Timer clockTimer;

    // Modern color scheme
    private Color primaryColor = new Color(41, 128, 185);      // Blue
    private Color secondaryColor = new Color(52, 152, 219);    // Lighter blue
    private Color accentColor = new Color(26, 188, 156);       // Teal
    private Color dangerColor = new Color(231, 76, 60);        // Red
    private Color warningColor = new Color(241, 196, 15);      // Yellow
    private Color successColor = new Color(46, 204, 113);      // Green
    private Color textColor = new Color(52, 73, 94);           // Dark blue-gray
    private Color lightTextColor = new Color(236, 240, 241);   // Light gray
    private Color backgroundColor = new Color(250, 250, 250);  // Off-white
    private Color cardColor = new Color(255, 255, 255);        // White
    private Color shadowColor = new Color(0, 0, 0, 20);        // Transparent black for shadows

    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font headingFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font cardTitleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font cardValueFont = new Font("Segoe UI", Font.BOLD, 36);

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("Student Learning Platform - " + user.getUsername());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/images/logo.png")).getImage());

        // Initialize the list models
        classListModel = new DefaultListModel<>();
        quizListModel = new DefaultListModel<>();
        assignmentListModel = new DefaultListModel<>();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("TabbedPane.selected", secondaryColor);
            UIManager.put("TabbedPane.contentAreaColor", backgroundColor);
            UIManager.put("TabbedPane.light", backgroundColor);
            UIManager.put("TabbedPane.highlight", backgroundColor);
            UIManager.put("TabbedPane.focus", secondaryColor);
            UIManager.put("TabbedPane.selectHighlight", secondaryColor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        loadData();
        startClock();
        setVisible(true);
    }

    private void initializeComponents() {
        // Main layout with a border to create margins
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Header panel with logo and user info
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with custom styling
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(headingFont);
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setForeground(textColor);
        customizeTabbedPane(tabbedPane);

        // Create panels for each tab
        JPanel dashboardPanel = createDashboardPanel();
        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();

        // Add custom icons to tabs
        ImageIcon dashboardIcon = createTabIcon("\uf015"); // Font Awesome home icon
        ImageIcon classesIcon = createTabIcon("\uf19d"); // Font Awesome graduation cap icon
        ImageIcon quizzesIcon = createTabIcon("\uf059"); // Font Awesome question circle icon
        ImageIcon assignmentsIcon = createTabIcon("\uf15b"); // Font Awesome file icon

        tabbedPane.addTab("Dashboard", dashboardIcon, dashboardPanel);
        tabbedPane.addTab("Classes", classesIcon, classesPanel);
        tabbedPane.addTab("Quizzes", quizzesIcon, quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsIcon, assignmentsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
    }

    private ImageIcon createTabIcon(String iconChar) {
        // This is a placeholder. In real implementation, you'd use actual icon resources
        // Here we're just creating a colored label as a placeholder
        JLabel iconLabel = new JLabel(iconChar);
        iconLabel.setForeground(primaryColor);
        iconLabel.setFont(new Font("FontAwesome", Font.PLAIN, 16));
        return new ImageIcon(); // Return empty icon as placeholder
    }

    private void customizeTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setOpaque(true);
        
        // Add some animation/effect when switching tabs
        tabbedPane.addChangeListener(e -> {
            SwingUtilities.invokeLater(() -> {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Component comp = tabbedPane.getComponentAt(selectedIndex);
                    comp.setVisible(false);
                    Timer timer = new Timer(50, event -> comp.setVisible(true));
                    timer.setRepeats(false);
                    timer.start();
                }
            });
        });
    }

    private JPanel createHeaderPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(backgroundColor);
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(cardColor);
        headerPanel.setBorder(createShadowBorder());
        
        // Logo and title section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        logoPanel.setBackground(cardColor);
        
        JLabel logoLabel = new JLabel("EduSync");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(primaryColor);
        
        logoPanel.add(logoLabel);
        
        // User profile section
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        profilePanel.setBackground(cardColor);
        
        // User avatar (circular)
        JPanel avatarPanel = createCircularAvatar(user.getUsername().substring(0, 1).toUpperCase());
        
        // User info and logout button
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setBackground(cardColor);
        
        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(headingFont);
        nameLabel.setForeground(textColor);
        
        JLabel roleLabel = new JLabel("Student");
        roleLabel.setFont(smallFont);
        roleLabel.setForeground(new Color(150, 150, 150));
        
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(roleLabel);
        
        JButton logoutButton = createIconButton("Logout", "\uf08b", dangerColor);
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
        
        profilePanel.add(userInfoPanel);
        profilePanel.add(avatarPanel);
        profilePanel.add(logoutButton);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(profilePanel, BorderLayout.EAST);
        
        outerPanel.add(headerPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    private JPanel createCircularAvatar(String initial) {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Draw circle background
                g2d.setColor(primaryColor);
                g2d.fillOval(x, y, size, size);
                
                // Draw initial
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initial);
                int textHeight = fm.getHeight();
                g2d.drawString(initial, x + (size - textWidth) / 2, 
                               y + ((size - textHeight) / 2) + fm.getAscent());
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(40, 40);
            }
        };
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(cardColor);
        welcomePanel.setBorder(createShadowBorder());
        
        JPanel welcomeInnerPanel = new JPanel(new BorderLayout(20, 10));
        welcomeInnerPanel.setBackground(cardColor);
        welcomeInnerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Greeting text with current time awareness
        String greeting;
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) greeting = "Good Morning";
        else if (hour < 18) greeting = "Good Afternoon";
        else greeting = "Good Evening";
        
        JLabel greetingLabel = new JLabel(greeting + ", " + user.getUsername() + "!");
        greetingLabel.setFont(titleFont);
        greetingLabel.setForeground(textColor);
        
        JLabel subtitleLabel = new JLabel("Welcome to your learning dashboard");
        subtitleLabel.setFont(regularFont);
        subtitleLabel.setForeground(new Color(130, 130, 130));
        
        JPanel greetingPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        greetingPanel.setBackground(cardColor);
        greetingPanel.add(greetingLabel);
        greetingPanel.add(subtitleLabel);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(cardColor);
        
        JButton scheduleButton = createIconButton("My Schedule", "\uf073", secondaryColor);
        JButton notificationsButton = createIconButton("Notifications", "\uf0f3", warningColor);
        
        actionsPanel.add(scheduleButton);
        actionsPanel.add(notificationsButton);
        
        welcomeInnerPanel.add(greetingPanel, BorderLayout.WEST);
        welcomeInnerPanel.add(actionsPanel, BorderLayout.EAST);
        welcomePanel.add(welcomeInnerPanel, BorderLayout.CENTER);

        // Main content with statistics and activities
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(backgroundColor);
        
        // Stats cards row
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(backgroundColor);
        
        // Add stat cards with beautiful styling
        statsPanel.add(createMetricCard("Classes", String.valueOf(classListModel.size()), primaryColor, "\uf19d"));
        statsPanel.add(createMetricCard("Quizzes", String.valueOf(quizListModel.size()), secondaryColor, "\uf059"));
        statsPanel.add(createMetricCard("Assignments", String.valueOf(assignmentListModel.size()), accentColor, "\uf15b"));
        statsPanel.add(createMetricCard("Progress", "76%", successColor, "\uf200"));
        
        // Activities and calendar section
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        detailsPanel.setBackground(backgroundColor);
        
        // Recent activities panel
        JPanel activitiesPanel = createSectionPanel("Recent Activities", true);
        JPanel activitiesContent = new JPanel();
        activitiesContent.setLayout(new BoxLayout(activitiesContent, BoxLayout.Y_AXIS));
        activitiesContent.setBackground(cardColor);
        
        // Add some sample activities
        activitiesContent.add(createActivityItem("Joined Advanced Math class", "Today, 10:30 AM", "\uf19d"));
        activitiesContent.add(createActivityItem("Submitted Physics Assignment", "Yesterday, 3:45 PM", "\uf15b"));
        activitiesContent.add(createActivityItem("Completed Computer Science Quiz", "May 07, 11:20 AM", "\uf059"));
        activitiesContent.add(createActivityItem("Joined Chemistry class", "May 06, 9:00 AM", "\uf19d"));
        
        JScrollPane activitiesScrollPane = new JScrollPane(activitiesContent);
        activitiesScrollPane.setBorder(null);
        activitiesScrollPane.setBackground(cardColor);
        customizeScrollPane(activitiesScrollPane);
        
        activitiesPanel.add(activitiesScrollPane, BorderLayout.CENTER);
        
        // Calendar/schedule panel
        JPanel calendarPanel = createSectionPanel("Upcoming Schedule", true);
        JPanel calendarContent = new JPanel();
        calendarContent.setLayout(new BoxLayout(calendarContent, BoxLayout.Y_AXIS));
        calendarContent.setBackground(cardColor);
        
        // Add sample schedule items
        calendarContent.add(createScheduleItem("Physics Lab", "Today, 2:00 PM - 4:00 PM", successColor));
        calendarContent.add(createScheduleItem("Math Quiz", "Tomorrow, 10:00 AM - 11:00 AM", warningColor));
        calendarContent.add(createScheduleItem("Computer Science", "Tomorrow, 1:00 PM - 3:00 PM", primaryColor));
        calendarContent.add(createScheduleItem("Chemistry Assignment Due", "May 12, 11:59 PM", dangerColor));
        
        JScrollPane calendarScrollPane = new JScrollPane(calendarContent);
        calendarScrollPane.setBorder(null);
        calendarScrollPane.setBackground(cardColor);
        customizeScrollPane(calendarScrollPane);
        
        calendarPanel.add(calendarScrollPane, BorderLayout.CENTER);
        
        detailsPanel.add(activitiesPanel);
        detailsPanel.add(calendarPanel);
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Performance chart section
        JPanel performancePanel = createSectionPanel("Performance Overview", false);
        performancePanel.setPreferredSize(new Dimension(0, 200));
        
        // Add simple chart placeholder (in real app, use JFreeChart or similar)
        JPanel chartPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int padding = 30;
                
                // Draw axes
                g2.setColor(new Color(200, 200, 200));
                g2.drawLine(padding, height - padding, width - padding, height - padding); // x-axis
                g2.drawLine(padding, padding, padding, height - padding); // y-axis
                
                // Draw data points
                int[] dataPoints = {65, 59, 80, 81, 56, 70, 75};
                int numPoints = dataPoints.length;
                int pointSpacing = (width - 2 * padding) / (numPoints - 1);
                
                // Draw data lines
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(3f));
                for (int i = 0; i < numPoints - 1; i++) {
                    int x1 = padding + i * pointSpacing;
                    int y1 = height - padding - (dataPoints[i] * (height - 2 * padding) / 100);
                    int x2 = padding + (i + 1) * pointSpacing;
                    int y2 = height - padding - (dataPoints[i + 1] * (height - 2 * padding) / 100);
                    g2.drawLine(x1, y1, x2, y2);
                }
                
                // Draw points
                for (int i = 0; i < numPoints; i++) {
                    int x = padding + i * pointSpacing;
                    int y = height - padding - (dataPoints[i] * (height - 2 * padding) / 100);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x - 5, y - 5, 10, 10);
                    g2.setColor(primaryColor);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(x - 5, y - 5, 10, 10);
                }
                
                // Draw labels
                g2.setColor(textColor);
                g2.setFont(smallFont);
                String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"};
                for (int i = 0; i < numPoints; i++) {
                    int x = padding + i * pointSpacing;
                    g2.drawString(labels[i], x - 10, height - padding + 20);
                }
            }
        };
        chartPlaceholder.setBackground(cardColor);
        
        performancePanel.add(chartPlaceholder, BorderLayout.CENTER);
        
        // Put everything together
        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(performancePanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = createContentPanel("My Classes");
        
        // Create a custom list renderer for classes
        JList<String> classList = new JList<>(classListModel);
        classList.setCellRenderer(new ClassListCellRenderer());
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.setFixedCellHeight(80);
        
        JScrollPane scrollPane = new JScrollPane(classList);
        scrollPane.setBorder(null);
        customizeScrollPane(scrollPane);
        
        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton joinButton = createStyledButton("Join Selected Class", accentColor);
        joinButton.addActionListener(e -> {
            String selected = classList.getSelectedValue();
            if (selected != null) {
                // Use the AttendanceManager class to process class joining
                int classId = AttendanceManager.getClassIdFromDisplayTitle(selected);
                
                if (classId == -1) {
                    showErrorDialog("Could not find class ID. Please try again or contact support.");
                    return;
                }
                
                // Retrieve the Google Meet link for the class
                String meetLink = AttendanceManager.getClassMeetLink(classId);
                if (meetLink != null && !meetLink.isEmpty()) {
                    if (isValidGoogleMeetLink(meetLink)) {
                        try {
                            Desktop.getDesktop().browse(new java.net.URI(meetLink));
                        } catch (Exception ex) {
                            showErrorDialog("Failed to open the class link. Please try again.");
                        }
                    } else {
                        showErrorDialog("The provided link is not a valid Google Meet link.");
                    }
                } else {
                    showErrorDialog("No Google Meet link found for the selected class.");
                }
            } else {
                showInfoDialog("Please select a class first");
            }
        });
        
        buttonPanel.add(joinButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = createContentPanel("Available Quizzes");
        
        // Create a custom list renderer for quizzes
        JList<String> quizList = new JList<>(quizListModel);
        quizList.setCellRenderer(new QuizListCellRenderer());
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setFixedCellHeight(80);
        
        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(null);
        customizeScrollPane(scrollPane);
        
        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton takeButton = createStyledButton("Take Selected Quiz", accentColor);
        takeButton.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) {
                // Show a confirmation dialog before starting quiz
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you ready to start the quiz?\nOnce started, you cannot pause.",
                    "Confirm Quiz Start",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    int quizId = getQuizIdFromTitle(selected);
                    int score = calculateQuizScore();
                    boolean success = DataManager.getInstance().takeQuiz(quizId, user.getId(), score);
                    if (success) {
                        showSuccessDialog("Quiz attempt recorded successfully!");
                    } else {
                        showErrorDialog("Failed to record quiz attempt.");
                    }
                }
            } else {
                showInfoDialog("Please select a quiz first");
            }
        });
        
        buttonPanel.add(takeButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = createContentPanel("My Assignments");
        
        // Create a custom list renderer for assignments
        JList<String> assignmentList = new JList<>(assignmentListModel);
        assignmentList.setCellRenderer(new AssignmentListCellRenderer());
        assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentList.setFixedCellHeight(80);
        
        JScrollPane scrollPane = new JScrollPane(assignmentList);
        scrollPane.setBorder(null);
        customizeScrollPane(scrollPane);
        
        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton submitButton = createStyledButton("Submit Selected Assignment", accentColor);
        submitButton.addActionListener(e -> {
            String selected = assignmentList.getSelectedValue();
            if (selected != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Assignment File");
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    int assignmentId = getAssignmentIdFromTitle(selected);
                    boolean success = DataManager.getInstance().submitAssignment(assignmentId, user.getId(), filePath);
                    if (success) {
                        showSuccessDialog("Assignment submitted successfully!");
                    } else {
                        showErrorDialog("Failed to submit assignment.");
                    }
                }
            } else {
                showInfoDialog("Please select an assignment first");
            }
        });
        
        buttonPanel.add(submitButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createContentPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(cardColor);
        titlePanel.setBorder(createShadowBorder());
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(headingFont);
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cardColor);
        contentPanel.setBorder(createShadowBorder());
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return contentPanel;
    }

    private JPanel createSectionPanel(String title, boolean fullHeight) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(cardColor);
        panel.setBorder(createShadowBorder());
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(cardColor);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(cardTitleFont);
        titleLabel.setForeground(textColor);
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        // If not full height, add a view all button
        if (!fullHeight) {
            JButton viewAllButton = new JButton("View All");
            viewAllButton.setFont(smallFont);
            viewAllButton.setBorderPainted(false);
            viewAllButton.setContentAreaFilled(false);
            viewAllButton.setForeground(primaryColor);
            viewAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            titleBar.add(viewAllButton, BorderLayout.EAST);
        }
        
        panel.add(titleBar, BorderLayout.NORTH);
        
        return panel;
    }

    private JPanel createMetricCard(String title, String value, Color color, String iconCode) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(cardColor);
        card.setBorder(createShadowBorder());
        card.setPreferredSize(new Dimension(0, 120));
        
        // Icon
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw icon background
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                int size = Math.min(getWidth(), getHeight()) - 20;
                g2d.fillRoundRect(10, 10, size, size, 10, 10);
                
                // Draw icon (in real app, use proper icon libraries)
                g2d.setColor(color);
                g2d.setFont(new Font("FontAwesome", Font.PLAIN, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(iconCode, 10 + (size - fm.stringWidth(iconCode)) / 2, 
                              10 + ((size - fm.getHeight()) / 2) + fm.getAscent());
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 0);
            }
        };
        iconPanel.setOpaque(false);
        
        // Text content
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        contentPanel.setBackground(cardColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 15));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(cardValueFont);
        valueLabel.setForeground(textColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(regularFont);
        titleLabel.setForeground(new Color(130, 130, 130));
        
        contentPanel.add(valueLabel);
        contentPanel.add(titleLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createActivityItem(String activity, String time, String iconCode) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setBackground(cardColor);
        item.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(30, 30));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 40));
        iconLabel.setForeground(primaryColor);
        iconLabel.setFont(new Font("FontAwesome", Font.PLAIN, 14));
        iconLabel.setText(iconCode);
        
        // Make the icon circular
        iconLabel.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c.getBackground());
                g2d.fillRoundRect(x, y, width, height, height, height);
                g2d.dispose();
            }
        });
        
        // Text content
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(cardColor);
        
        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(regularFont);
        activityLabel.setForeground(textColor);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(smallFont);
        timeLabel.setForeground(new Color(150, 150, 150));
        
        textPanel.add(activityLabel);
        textPanel.add(timeLabel);
        
        item.add(iconLabel, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    private JPanel createScheduleItem(String title, String time, Color color) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setBackground(cardColor);
        item.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Color indicator
        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(5, 0));
        colorIndicator.setBackground(color);
        
        // Text content
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(cardColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(regularFont);
        titleLabel.setForeground(textColor);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(smallFont);
        timeLabel.setForeground(new Color(150, 150, 150));
        
        textPanel.add(titleLabel);
        textPanel.add(timeLabel);
        
        // Join button for classes
        JButton joinButton = new JButton("Join");
        joinButton.setFont(smallFont);
        joinButton.setForeground(Color.WHITE);
        joinButton.setBackground(color);
        joinButton.setBorderPainted(false);
        joinButton.setFocusPainted(false);
        joinButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        joinButton.setPreferredSize(new Dimension(60, 30));
        
        item.add(colorIndicator, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        item.add(joinButton, BorderLayout.EAST);
        
        return item;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(cardColor);
        statusBar.setBorder(createShadowBorder());
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // System status on the left
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(cardColor);
        
        JLabel statusIcon = new JLabel("•");
        statusIcon.setForeground(successColor);
        statusIcon.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel statusText = new JLabel("Connected to Learning Server");
        statusText.setFont(smallFont);
        statusText.setForeground(new Color(130, 130, 130));
        
        leftPanel.add(statusIcon);
        leftPanel.add(statusText);

        // Date and time on the right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(cardColor);
        
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(smallFont);
        clockLabel.setForeground(new Color(130, 130, 130));
        updateClock(clockLabel); // Set initial time
        
        rightPanel.add(clockLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        return statusBar;
    }

    private void startClock() {
        JLabel clockLabel = new JLabel();
        clockTimer = new Timer(1000, e -> updateClock(clockLabel));
        clockTimer.start();
    }
    
    private void updateClock(JLabel clockLabel) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm:ss a");
        clockLabel.setText(sdf.format(new Date()));
    }

    private Border createShadowBorder() {
        return BorderFactory.createCompoundBorder(
            new ShadowBorder(5, 0.2f),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        );
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add padding and border
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Custom UI for rounded corners
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Get button dimensions and state
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();
                Color buttonColor = button.getBackground();
                
                // Adjust color based on button state
                if (model.isPressed()) {
                    buttonColor = buttonColor.darker();
                } else if (model.isRollover()) {
                    buttonColor = buttonColor.brighter();
                }

                // Draw button background
                g2.setColor(buttonColor);
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);

                // Draw text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(button.getText(), g2).getBounds();
                int textX = (button.getWidth() - stringBounds.width) / 2;
                int textY = (button.getHeight() - stringBounds.height) / 2 + fm.getAscent();
                g2.setColor(button.getForeground());
                g2.drawString(button.getText(), textX, textY);
            }
        });

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }
    
    private JButton createIconButton(String text, String iconCode, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        button.setForeground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            }
        });
        
        return button;
    }

    private void customizeScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBackground(cardColor);
        scrollPane.getViewport().setBackground(cardColor);
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
        
        // Refresh the dashboard
        tabbedPane.setComponentAt(0, createDashboardPanel());
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private int getAssignmentIdFromTitle(String title) {
        // Implementation to extract assignment ID from title
        return 0; // Placeholder implementation
    }

    private int getClassIdFromTitle(String title) {
        // Implementation to extract class ID from title
        return 0; // Placeholder implementation
    }

    private int getQuizIdFromTitle(String title) {
        // Implementation to extract quiz ID from title
        return 0; // Placeholder implementation
    }

    private int calculateQuizScore() {
        // Implementation to calculate quiz score
        return (int)(Math.random() * 100); // Placeholder implementation
    }

    private boolean isValidGoogleMeetLink(String link) {
        String googleMeetRegex = "^(https:\\/\\/)?meet\\.google\\.com\\/([a-zA-Z0-9\\-]+)$";
        return link.matches(googleMeetRegex);
    }

    // Custom list cell renderers
    private class ClassListCellRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel titleLabel = new JLabel();
        private JLabel infoLabel = new JLabel();
        private JLabel timeLabel = new JLabel();
        private JPanel colorBar = new JPanel();
        
        public ClassListCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            // Color bar on the left
            colorBar.setPreferredSize(new Dimension(5, 0));
            
            // Text panel in the center
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            textPanel.setOpaque(false);
            
            titleLabel.setFont(regularFont);
            infoLabel.setFont(smallFont);
            infoLabel.setForeground(new Color(120, 120, 120));
            
            textPanel.add(titleLabel);
            textPanel.add(infoLabel);
            
            // Time/status on the right
            timeLabel.setFont(smallFont);
            timeLabel.setForeground(new Color(150, 150, 150));
            timeLabel.setHorizontalAlignment(JLabel.RIGHT);
            
            add(colorBar, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
            add(timeLabel, BorderLayout.EAST);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, 
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            // Set the text for the renderer components
            titleLabel.setText(value);
            
            // Extract info from the value (in real app, use proper data model)
            String[] parts = value.split(" - ");
            if (parts.length > 1) {
                titleLabel.setText(parts[0]);
                infoLabel.setText(parts[1]);
            } else {
                infoLabel.setText("Course Information");
            }
            
            // Set time based on index (placeholder)
            String[] times = {"10:00 AM - 11:30 AM", "1:00 PM - 2:30 PM", "3:00 PM - 4:30 PM"};
            timeLabel.setText(times[index % times.length]);
            
            // Set color based on index (placeholder)
            Color[] colors = {primaryColor, secondaryColor, accentColor, warningColor};
            colorBar.setBackground(colors[index % colors.length]);
            
            // Handle selection state
            if (isSelected) {
                setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 30));
                setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            } else {
                setBackground(list.getBackground());
                setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            setEnabled(list.isEnabled());
            setOpaque(true);
            
            return this;
        }
    }
    
    private class QuizListCellRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel titleLabel = new JLabel();
        private JLabel infoLabel = new JLabel();
        private JLabel statusLabel = new JLabel();
        
        public QuizListCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            // Text panel in the center
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            textPanel.setOpaque(false);
            
            titleLabel.setFont(regularFont);
            infoLabel.setFont(smallFont);
            infoLabel.setForeground(new Color(120, 120, 120));
            
            textPanel.add(titleLabel);
            textPanel.add(infoLabel);
            
            // Status on the right
            statusLabel.setFont(smallFont);
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setHorizontalAlignment(JLabel.CENTER);
            statusLabel.setOpaque(true);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            add(textPanel, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.EAST);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, 
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            titleLabel.setText(value);
            
            // Extract info from the value (in real app, use proper data model)
            infoLabel.setText("Duration: 60 minutes • Questions: " + (10 + index));
            
            // Set status based on index (placeholder)
            String[] statuses = {"Available", "Upcoming", "Available"};
            Color[] statusColors = {accentColor, warningColor, accentColor};
            statusLabel.setText(statuses[index % statuses.length]);
            statusLabel.setBackground(statusColors[index % statusColors.length]);
            
            // Handle selection state
            if (isSelected) {
                setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
            } else {
                setBackground(list.getBackground());
            }
            
            setEnabled(list.isEnabled());
            setOpaque(true);
            
            return this;
        }
    }
    
    private class AssignmentListCellRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel titleLabel = new JLabel();
        private JLabel infoLabel = new JLabel();
        private JLabel dueDateLabel = new JLabel();
        
        public AssignmentListCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            // Text panel in the center
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            textPanel.setOpaque(false);
            
            titleLabel.setFont(regularFont);
            infoLabel.setFont(smallFont);
            infoLabel.setForeground(new Color(120, 120, 120));
            
            textPanel.add(titleLabel);
            textPanel.add(infoLabel);
            
            // Due date on the right
            dueDateLabel.setFont(smallFont);
            dueDateLabel.setHorizontalAlignment(JLabel.RIGHT);
            
            add(textPanel, BorderLayout.CENTER);
            add(dueDateLabel, BorderLayout.EAST);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, 
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            titleLabel.setText(value);
            
            // Extract info from the value (in real app, use proper data model)
            String[] subjects = {"Math", "Computer Science", "Physics", "Chemistry"};
            infoLabel.setText(subjects[index % subjects.length] + " • Points: " + (100 - (index * 10)));
            
            // Set due date and color based on index (placeholder)
            String[] dueDates = {"Due Tomorrow", "Due in 3 days", "Due next week", "Overdue by 2 days"};
            Color[] dateColors = {warningColor, accentColor, primaryColor, dangerColor};
            dueDateLabel.setText(dueDates[index % dueDates.length]);
            dueDateLabel.setForeground(dateColors[index % dateColors.length]);
            
            // Handle selection state
            if (isSelected) {
                setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 30));
            } else {
                setBackground(list.getBackground());
            }
            
            setEnabled(list.isEnabled());
            setOpaque(true);
            
            return this;
        }
    }
    
    // Custom shadow border class
    private class ShadowBorder extends AbstractBorder {
        private int shadowSize;
        private float shadowOpacity;
        
        public ShadowBorder(int size, float opacity) {
            this.shadowSize = size;
            this.shadowOpacity = opacity;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the shadow
            for (int i = 0; i < shadowSize; i++) {
                g2d.setColor(new Color(0, 0, 0, (int)(shadowOpacity * 255 / shadowSize * (shadowSize - i))));
                g2d.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 10, 10);
            }
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = shadowSize;
            return insets;
        }
    }
    
    // Modern scrollbar UI
    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = Color.WHITE;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw thumb
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                            thumbBounds.width, thumbBounds.height, 
                            10, 10);
            
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = new User(1, "JohnDoe", "student");
            new StudentDashboard(user);
        });
    }
}