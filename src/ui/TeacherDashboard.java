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

    public TeacherDashboard(User user) {
        this.user = user;
        setTitle("Teacher Dashboard - Welcome " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        loadData(); // Load data from the database
        setVisible(true);
    }

    private void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel classesPanel = createClassesPanel();
        JPanel quizzesPanel = createQuizzesPanel();
        JPanel assignmentsPanel = createAssignmentsPanel();

        tabbedPane.addTab("Classes", classesPanel);
        tabbedPane.addTab("Quizzes", quizzesPanel);
        tabbedPane.addTab("Assignments", assignmentsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        classListModel = new DefaultListModel<>();
        JList<String> classList = new JList<>(classListModel);

        JButton addClassButton = new JButton("Add Class");
        addClassButton.addActionListener(e -> {
            String className = JOptionPane.showInputDialog(this, "Enter class name:");
            if (className != null && !className.trim().isEmpty()) {
                String meetLink = JOptionPane.showInputDialog(this, "Enter Google Meet link:");
                if (meetLink != null && !meetLink.trim().isEmpty()) {
                    boolean success = DataManager.getInstance().addClass(className, meetLink, user.getId());
                    if (success) {
                        classListModel.addElement(className + " - Meet: " + meetLink);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add class.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(new JScrollPane(classList), BorderLayout.CENTER);
        panel.add(addClassButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        quizListModel = new DefaultListModel<>();
        JList<String> quizList = new JList<>(quizListModel);

        JButton addQuizButton = new JButton("Add Quiz");
        addQuizButton.addActionListener(e -> {
            String quizTitle = JOptionPane.showInputDialog(this, "Enter quiz title:");
            if (quizTitle != null && !quizTitle.trim().isEmpty()) {
                LocalDate dueDate = LocalDate.now().plusDays(7);
                boolean success = DataManager.getInstance().addQuiz(quizTitle, 1, dueDate); // Replace `1` with actual classId
                if (success) {
                    quizListModel.addElement(quizTitle + " - Due: " + dueDate);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add quiz.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(new JScrollPane(quizList), BorderLayout.CENTER);
        panel.add(addQuizButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        assignmentListModel = new DefaultListModel<>();
        JList<String> assignmentList = new JList<>(assignmentListModel);

        JButton addAssignmentButton = new JButton("Add Assignment");
        addAssignmentButton.addActionListener(e -> {
            String assignmentTitle = JOptionPane.showInputDialog(this, "Enter assignment title:");
            if (assignmentTitle != null && !assignmentTitle.trim().isEmpty()) {
                LocalDate dueDate = LocalDate.now().plusDays(14);
                boolean success = DataManager.getInstance().addAssignment(assignmentTitle, 1, dueDate); // Replace `1` with actual classId
                if (success) {
                    assignmentListModel.addElement(assignmentTitle + " - Due: " + dueDate);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add assignment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(new JScrollPane(assignmentList), BorderLayout.CENTER);
        panel.add(addAssignmentButton, BorderLayout.SOUTH);
        return panel;
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