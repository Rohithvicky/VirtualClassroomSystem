package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
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

        // Add icons to tabs
        ImageIcon classIcon = createIcon("class_icon.png", "Class");
        ImageIcon quizIcon = createIcon("quiz_icon.png", "Quiz");
        ImageIcon assignmentIcon = createIcon("assignment_icon.png", "Assignment");

        tabbedPane.addTab("Classes", classIcon, classesPanel);
        tabbedPane.addTab("Quizzes", quizIcon, quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentIcon, assignmentsPanel);

        // Add tabbed pane to main frame
        add(tabbedPane, BorderLayout.CENTER);

        // Add status bar at the bottom
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private ImageIcon createIcon(String path, String fallback) {
        try {
            return new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/" + path)));
        } catch (Exception e) {
            // If icon not found, return null (no icon will be displayed)
            return null;
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Welcome message with user info
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        // Create logout button using the createStyledButton method
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

        // Add components to header panel
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Manage Your Classes");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Create list panel
        classListModel = new DefaultListModel<>();
        JList<String> classList = new JList<>(classListModel);
        classList.setFont(regularFont);
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.setFixedCellHeight(40);
        classList.setCellRenderer(new CustomListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(classList);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton addClassButton = createStyledButton("Add Class", accentColor);
        JButton editClassButton = createStyledButton("Edit Class", accentColor);
        JButton deleteClassButton = createStyledButton("Delete Class", accentColor);

        buttonPanel.add(addClassButton);
        buttonPanel.add(editClassButton);
        buttonPanel.add(deleteClassButton);

        // Add action listeners
        addClassButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 10));
            JTextField className = new JTextField();
            JTextField meetLink = new JTextField();

            inputPanel.add(new JLabel("Class Name:"));
            inputPanel.add(className);
            inputPanel.add(new JLabel("Google Meet Link:"));
            inputPanel.add(meetLink);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Add New Class", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && !className.getText().trim().isEmpty()) {
                boolean success = DataManager.getInstance().addClass(
                        className.getText().trim(),
                        meetLink.getText().trim(),
                        user.getId());

                if (success) {
                    classListModel.addElement(className.getText() + " - Meet: " + meetLink.getText());
                    JOptionPane.showMessageDialog(this,
                            "Class added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add class.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editClassButton.addActionListener(e -> {
            int selectedIndex = classList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selected = classListModel.getElementAt(selectedIndex);
                String[] parts = selected.split(" - Meet: ");
                String currentClassName = parts[0];
                String currentMeetLink = parts.length > 1 ? parts[1] : "";

                JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 10));
                JTextField className = new JTextField(currentClassName);
                JTextField meetLink = new JTextField(currentMeetLink);

                inputPanel.add(new JLabel("Class Name:"));
                inputPanel.add(className);
                inputPanel.add(new JLabel("Google Meet Link:"));
                inputPanel.add(meetLink);

                int result = JOptionPane.showConfirmDialog(this, inputPanel,
                        "Edit Class", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION && !className.getText().trim().isEmpty()) {
                    boolean success = DataManager.getInstance().updateClass(
                            currentClassName, // Pass the old class name to identify the record
                            className.getText().trim(),
                            meetLink.getText().trim(),
                            user.getId());

                    if (success) {
                        classListModel.set(selectedIndex, className.getText() + " - Meet: " + meetLink.getText());
                        JOptionPane.showMessageDialog(this,
                                "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update class.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a class to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteClassButton.addActionListener(e -> {
            int selectedIndex = classList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selected = classListModel.getElementAt(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete: " + selected + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = DataManager.getInstance().deleteClass(selected.split(" - Meet: ")[0]);
                    if (success) {
                        classListModel.remove(selectedIndex);
                        JOptionPane.showMessageDialog(this,
                                "Class deleted successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete class.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a class to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add components to panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Manage Your Quizzes");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Create list panel
        quizListModel = new DefaultListModel<>();
        JList<String> quizList = new JList<>(quizListModel);
        quizList.setFont(regularFont);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setFixedCellHeight(40);
        quizList.setCellRenderer(new CustomListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));

        // Create button panel (removed edit button)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton addQuizButton = createStyledButton("Add Quiz", accentColor);
        JButton deleteQuizButton = createStyledButton("Delete Quiz", accentColor);

        buttonPanel.add(addQuizButton);
        buttonPanel.add(deleteQuizButton);

        // Add action listeners (kept only add and delete)
        addQuizButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 10));
            JTextField quizTitle = new JTextField();
            JComboBox<String> classSelector = new JComboBox<>();
            JSpinner dueDateSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
            JButton uploadButton = new JButton("Upload Document");
            JLabel fileLabel = new JLabel("No file selected");

            // Populate class selector
            classSelector.addItem("Select a class");
            for (int i = 0; i < classListModel.getSize(); i++) {
                String className = classListModel.getElementAt(i).split(" - ")[0];
                classSelector.addItem(className);
            }

            uploadButton.addActionListener(uploadEvent -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileLabel.setText(fileChooser.getSelectedFile().getName());
                }
            });

            inputPanel.add(new JLabel("Quiz Title:"));
            inputPanel.add(quizTitle);
            inputPanel.add(new JLabel("Select Class:"));
            inputPanel.add(classSelector);
            inputPanel.add(new JLabel("Due in (days):"));
            inputPanel.add(dueDateSpinner);
            inputPanel.add(uploadButton);
            inputPanel.add(fileLabel);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Add New Quiz", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && !quizTitle.getText().trim().isEmpty()
                    && classSelector.getSelectedIndex() > 0) {

                LocalDate dueDate = LocalDate.now().plusDays((int) dueDateSpinner.getValue());

                // Save quiz and document to the database
                boolean success = DataManager.getInstance().addQuizWithDocument(
                        quizTitle.getText().trim(),
                        1, // Replace with actual class ID
                        dueDate,
                        fileLabel.getText()); // Save the document name or path

                if (success) {
                    quizListModel.addElement(quizTitle.getText().trim() + " - Due: " + dueDate);
                    quizList.repaint();
                    JOptionPane.showMessageDialog(this,
                            "Quiz added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                        quizList.repaint();
                        loadData();
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

        // Add components to panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Manage Your Assignments");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Create list panel
        assignmentListModel = new DefaultListModel<>();
        JList<String> assignmentList = new JList<>(assignmentListModel);
        assignmentList.setFont(regularFont);
        assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentList.setFixedCellHeight(40);
        assignmentList.setCellRenderer(new CustomListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(assignmentList);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));

        // Create button panel (removed edit button)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton addAssignmentButton = createStyledButton("Add Assignment", accentColor);
        JButton deleteAssignmentButton = createStyledButton("Delete Assignment", accentColor);

        buttonPanel.add(addAssignmentButton);
        buttonPanel.add(deleteAssignmentButton);

        // Add action listeners (kept only add and delete)
        addAssignmentButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
            JTextField assignmentTitle = new JTextField();
            JComboBox<String> classSelector = new JComboBox<>();
            JSpinner dueDateSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 60, 1));

            // Populate class selector with actual class names
            classSelector.addItem("Select a class");
            for (int i = 0; i < classListModel.getSize(); i++) {
                String className = classListModel.getElementAt(i).split(" - ")[0];
                classSelector.addItem(className);
            }

            inputPanel.add(new JLabel("Assignment Title:"));
            inputPanel.add(assignmentTitle);
            inputPanel.add(new JLabel("Select Class:"));
            inputPanel.add(classSelector);
            inputPanel.add(new JLabel("Due in (days):"));
            inputPanel.add(dueDateSpinner);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Add New Assignment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && !assignmentTitle.getText().trim().isEmpty()
                    && classSelector.getSelectedIndex() > 0) {

                LocalDate dueDate = LocalDate.now().plusDays((int) dueDateSpinner.getValue());
                String selectedClass = classSelector.getSelectedItem().toString();

                // Fetch the class_id for the selected class
                int classId = DataManager.getInstance().getClassIdByName(selectedClass);

                if (classId == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid class selected. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add the assignment to the database
                boolean success = DataManager.getInstance().addAssignment(
                        assignmentTitle.getText().trim(),
                        classId,
                        dueDate);

                if (success) {
                    assignmentListModel.addElement(assignmentTitle.getText() + " - Due: " + dueDate);
                    JOptionPane.showMessageDialog(this,
                            "Assignment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add assignment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields.",
                        "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteAssignmentButton.addActionListener(e -> {
            int selectedIndex = assignmentList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selected = assignmentListModel.getElementAt(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete: " + selected + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Extract assignment title from the list item
                    String assignmentTitle = selected.split(" - Due: ")[0];

                    // Delete from database
                    boolean success = DataManager.getInstance().deleteAssignment(assignmentTitle);

                    if (success) {
                        assignmentListModel.remove(selectedIndex);
                        JOptionPane.showMessageDialog(this,
                                "Assignment deleted successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete assignment.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select an assignment to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add components to panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        // Add rounded corners and padding
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2), // Darker border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
        ));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter()); // Lighter color on hover
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

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(237, 242, 247));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Current date and status info
        JLabel statusLabel = new JLabel("Connected to Virtual Classroom Server • " +
                LocalDate.now().toString());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                label.setBackground(accentColor);
                label.setForeground(Color.WHITE);
            } else if (index % 2 == 0) {
                label.setBackground(new Color(240, 248, 255)); // Light blue for even rows
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return label;
        }
    }

    private void loadData() {
        DataManager dm = DataManager.getInstance();

        // Load classes
        classListModel.clear();
        dm.getClasses().forEach(classListModel::addElement);

        // Load quizzes with consistent format
        quizListModel.clear();
        dm.getQuizzes().forEach(quiz -> {
            if (!quiz.contains(" - Due: ")) {
                quizListModel.addElement(quiz + " - Due: " + LocalDate.now());
            } else {
                quizListModel.addElement(quiz);
            }
        });

        // Load assignments
        assignmentListModel.clear();
        dm.getAssignments().forEach(assignmentListModel::addElement);
    }
}