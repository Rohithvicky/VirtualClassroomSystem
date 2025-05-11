package model;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DataManager {
    private static DataManager instance;

    private DataManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // Record quiz result
    public boolean recordQuizResult(int studentId, int quizId, int score, int totalQuestions) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quiz_results (student_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.setInt(4, totalQuestions);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when recording quiz result: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get quizzes as Quiz objects with proper object handling
    public List<Quiz> getQuizzesObj() {
        List<Quiz> quizzes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id, title, due_date FROM quizzes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("due_date").toLocalDate()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return quizzes;
    }

    // Get assignments as Assignment objects with proper object handling
    public List<Assignment> getAssignmentsObj() {
        List<Assignment> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id, title, due_date FROM assignments";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(new Assignment(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("due_date").toLocalDate()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching assignments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return assignments;
    }

    // Get assignment ID by title
    public int getAssignmentIdFromTitle(String title) {
        return getAssignmentsObj().stream()
                .filter(a -> a.getTitle().equals(title))
                .findFirst()
                .map(Assignment::getId)
                .orElse(-1);
    }

    // Get quiz ID by title
    public int getQuizIdByTitle(String quizTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id FROM quizzes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1; // Quiz not found
    }

    // Get a Quiz object by its title
    public Quiz getQuiz(String quizTitle) {
        int quizId = getQuizIdByTitle(quizTitle);
        if (quizId == -1) {
            return null;
        }
        for (Quiz q : getQuizzesObj()) {
            if (q.getTitle().equals(quizTitle)) {
                return q;
            }
        }
        return null;
    }

    // Get quiz questions by quiz id
    public List<String[]> getQuizQuestions(int quizId) {
        List<String[]> questions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT question, option_a, option_b, option_c, option_d, correct_option FROM quiz_questions WHERE quiz_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String question = rs.getString("question");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                String correctOption = rs.getString("correct_option");
                questions.add(new String[] { question, optionA, optionB, optionC, optionD, correctOption });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return questions;
    }

    // Get quiz questions by title
    public List<String[]> getQuizQuestions(String quizTitle) {
        int quizId = getQuizIdByTitle(quizTitle);
        if (quizId == -1) {
            return new ArrayList<>();
        }
        return getQuizQuestions(quizId);
    }

    // Get all classes as a list of display strings
    public List<String> getClasses() {
        List<String> classes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT title, meet_link FROM classes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String meetLink = rs.getString("meet_link");
                classes.add(title + " - Meet: " + meetLink);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching classes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    // Get classes for a specific student as display strings
    public List<String> getClassesForStudent(int studentId) {
        List<String> classes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT c.title, c.meet_link FROM classes c " +
                    "JOIN student_classes sc ON c.id = sc.class_id " +
                    "WHERE sc.student_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String meetLink = rs.getString("meet_link");
                classes.add(title + " - Meet: " + meetLink);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching student classes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    // Get all quizzes (display strings)
    public List<String> getQuizzes() {
        List<String> quizzes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT title, due_date FROM quizzes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                quizzes.add(title + " - Due: " + dueDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return quizzes;
    }

    // Get quizzes for a specific student (display strings)
    public List<String> getQuizzesForStudent(int studentId) {
        List<String> quizzes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT q.id, q.title, q.due_date FROM quizzes q " +
                    "JOIN classes c ON q.class_id = c.id " +
                    "JOIN student_classes sc ON c.id = sc.class_id " +
                    "WHERE sc.student_id = ? " +
                    "AND NOT EXISTS (SELECT 1 FROM quiz_attempts qa WHERE qa.quiz_id = q.id AND qa.student_id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String dueDate = rs.getString("due_date");
                quizzes.add(title + " - Due: " + dueDate);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching student quizzes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return quizzes;
    }

    // Get all assignments (display strings)
    public List<String> getAssignments() {
        List<String> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT title, due_date FROM assignments";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String dueDate = rs.getString("due_date");
                assignments.add(title + " - Due: " + dueDate);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching assignments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return assignments;
    }

    // Get assignments for a specific student (display strings)
    public List<String> getAssignmentsForStudent(int studentId) {
        List<String> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.id, a.title, a.due_date FROM assignments a " +
                    "JOIN classes c ON a.class_id = c.id " +
                    "JOIN student_classes sc ON c.id = sc.class_id " +
                    "WHERE sc.student_id = ? " +
                    "AND NOT EXISTS (SELECT 1 FROM submissions s WHERE s.assignment_id = a.id AND s.student_id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String dueDate = rs.getString("due_date");
                assignments.add(title + " - Due: " + dueDate);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when fetching student assignments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return assignments;
    }

    // Take a quiz (record quiz attempt)
    public boolean takeQuiz(int quizId, int studentId, int score) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quiz_attempts (quiz_id, student_id, attempt_date, score) VALUES (?, ?, NOW(), ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            stmt.setInt(2, studentId);
            stmt.setInt(3, score);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when taking quiz: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Attend a class (mark attendance)
    public boolean attendClass(int classId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO attendance (class_id, user_id, attendance_date) VALUES (?, ?, CURRENT_DATE())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when marking attendance: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Submit an assignment (record submission)
    public boolean submitAssignment(int assignmentId, int studentId, String filePath) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO submissions (assignment_id, student_id, submission_date, file_path) VALUES (?, ?, NOW(), ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            stmt.setString(3, filePath);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when submitting assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add an assignment with a description (if classId is -1, do not include it)
    public boolean addAssignment(String title, int classId, LocalDate dueDate, String description) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = classId == -1
                    ? "INSERT INTO assignments (title, due_date, description) VALUES (?, ?, ?)"
                    : "INSERT INTO assignments (title, class_id, due_date, description) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            if (classId != -1) {
                stmt.setInt(2, classId);
                stmt.setDate(3, java.sql.Date.valueOf(dueDate));
                stmt.setString(4, description);
            } else {
                stmt.setDate(2, java.sql.Date.valueOf(dueDate));
                stmt.setString(3, description);
            }
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when adding assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete an assignment by title
    public boolean deleteAssignment(String assignmentTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM assignments WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, assignmentTitle);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when deleting assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get class ID by class name
    public int getClassIdByName(String className) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id FROM classes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, className);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    // ----- Additional Methods to Support Teacher Dashboard -----

    // Add a class with title, meet link and teacher id
    public boolean addClass(String title, String meetLink, int teacherId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO classes (title, meet_link, teacher_id) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, meetLink);
            stmt.setInt(3, teacherId);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when adding class: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update a class given the old title, new title, new meet link, and teacher id
    public boolean updateClass(String oldTitle, String newTitle, String newMeetLink, int teacherId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE classes SET title = ?, meet_link = ? WHERE title = ? AND teacher_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newTitle);
            stmt.setString(2, newMeetLink);
            stmt.setString(3, oldTitle);
            stmt.setInt(4, teacherId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when updating class: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete a class by title
    public boolean deleteClass(String classTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM classes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, classTitle);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when deleting class: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add a quiz with title, class id, and due date
    public boolean addQuiz(String quizTitle, int classId, LocalDate dueDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quizzes (title, class_id, due_date) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            stmt.setInt(2, classId);
            stmt.setDate(3, java.sql.Date.valueOf(dueDate));
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when adding quiz: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add a quiz question
    public boolean addQuizQuestion(int quizId, String question, String optionA, String optionB, String optionC,
            String optionD, String correctOption) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quiz_questions (quiz_id, question, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            stmt.setString(2, question);
            stmt.setString(3, optionA);
            stmt.setString(4, optionB);
            stmt.setString(5, optionC);
            stmt.setString(6, optionD);
            stmt.setString(7, correctOption);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when adding quiz question: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete a quiz by title
    public boolean deleteQuiz(String quizTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM quizzes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when deleting quiz: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}