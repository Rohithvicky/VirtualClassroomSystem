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

    // Add a class to the database
    public boolean addClass(String className, String meetLink, int teacherId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO classes (title, meet_link, created_by) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, className);
            stmt.setString(2, meetLink);
            stmt.setInt(3, teacherId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    // Update a class in the database
    public boolean updateClass(String oldClassName, String newClassName, String meetLink, int teacherId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE classes SET title = ?, meet_link = ? WHERE title = ? AND created_by = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newClassName);
            stmt.setString(2, meetLink);
            stmt.setString(3, oldClassName);
            stmt.setInt(4, teacherId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    // Delete a class from the database
    public boolean deleteClass(String className) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM classes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, className);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    // Add a quiz to the database
    public boolean addQuiz(String quizTitle, int classId, LocalDate dueDate) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quizzes (title, class_id, due_date) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            stmt.setInt(2, classId);
            stmt.setString(3, dueDate.toString());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    public boolean addQuiz(String title, String question, String optionA, String optionB, String optionC,
            String optionD, String correctAnswer) {
        // Save the quiz to the database or a file
        // Example: Save to a database table or JSON file
        return true; // Return true if successful
    }

    public Quiz getQuiz(String quizTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM quizzes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Quiz(
                        rs.getString("title"),
                        rs.getString("document_name"),
                        rs.getDate("due_date").toLocalDate());
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
        return null;
    }

    public boolean addQuizWithDocument(String quizTitle, int classId, LocalDate dueDate, String documentName) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quizzes (title, class_id, due_date, document_name) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            stmt.setInt(2, classId);
            stmt.setDate(3, java.sql.Date.valueOf(dueDate));
            stmt.setString(4, documentName);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    public boolean updateQuiz(String oldQuizTitle, String newQuizTitle, int classId, LocalDate dueDate) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE quizzes SET title = ?, class_id = ?, due_date = ? WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newQuizTitle);
            stmt.setInt(2, classId);
            stmt.setDate(3, java.sql.Date.valueOf(dueDate));
            stmt.setString(4, oldQuizTitle);

            System.out.println("Executing query: " + stmt);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when updating quiz: " + e.getMessage(),
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

    public boolean deleteQuiz(String quizTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM quizzes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (SQLException e) {
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

    // Add an assignment to the database
    public boolean addAssignment(String title, int classId, LocalDate dueDate) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO assignments (title, class_id, due_date) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, classId);
            stmt.setString(3, dueDate.toString());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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

    // Get all classes
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

    // Get classes for a specific student
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

    // Get all quizzes
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
                String dueDate = rs.getString("due_date");
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

    // Get quizzes for a specific student
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

    // Get all assignments
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

    // Get assignments for a specific student
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

    // Take a quiz
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

    // Attend a class
    public boolean attendClass(int classId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO attendance (class_id, user_id, attendance_date) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            stmt.setInt(2, userId);
            stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error when attending class: " + e.getMessage(),
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

    // Submit an assignment
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
}