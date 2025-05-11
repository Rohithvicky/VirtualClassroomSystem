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
            stmt.setDate(3, java.sql.Date.valueOf(dueDate));

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

    public boolean addQuizQuestion(String quizTitle, String question, String optionA, String optionB, String optionC,
            String optionD, String correctOption) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO quiz_questions (quiz_title, question, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
            stmt.setString(2, question);
            stmt.setString(3, optionA);
            stmt.setString(4, optionB);
            stmt.setString(5, optionC);
            stmt.setString(6, optionD);
            stmt.setString(7, correctOption);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
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

    // Add an assignment to the database with description
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

    // Delete an assignment from the database by title
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

    public List<String[]> getQuizQuestions(String quizTitle) {
        List<String[]> questions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT question, option_a, option_b, option_c, option_d, correct_option FROM quiz_questions WHERE quiz_title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, quizTitle);
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
        return -1; // Return -1 if class not found
    }
}