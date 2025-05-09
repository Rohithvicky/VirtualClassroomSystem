package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Manages MySQL database connections for the Virtual Classroom application.
 */
public class DBConnection {
    
    // MySQL connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/virtual_classroom";
    private static final String DB_USER = "root"; // Change to your MySQL username
    private static final String DB_PASSWORD = "Rohith@123"; // Change to your MySQL password
    
    /**
     * Get a connection to the MySQL database
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create the connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Initialize database if it's the first run
            initializeDatabase(conn);
            
            return conn;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC driver not found: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL connection error: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Initializes the MySQL database with required tables if they don't exist
     */
    private static void initializeDatabase(Connection conn) throws SQLException {
        // Users table
        String createUsersTable = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "password VARCHAR(100) NOT NULL, " +
            "role ENUM('teacher', 'student') NOT NULL)";
        
        // Classes table
        String createClassesTable = 
            "CREATE TABLE IF NOT EXISTS classes (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "title VARCHAR(100) NOT NULL, " +
            "meet_link VARCHAR(255) NOT NULL, " +
            "created_by INT NOT NULL, " +
            "FOREIGN KEY (created_by) REFERENCES users(id))";
            
        // Student-Class enrollments
        String createEnrollmentsTable = 
            "CREATE TABLE IF NOT EXISTS enrollments (" +
            "student_id INT NOT NULL, " +
            "class_id INT NOT NULL, " +
            "PRIMARY KEY (student_id, class_id), " +
            "FOREIGN KEY (student_id) REFERENCES users(id), " +
            "FOREIGN KEY (class_id) REFERENCES classes(id))";
            
        // Quizzes table
        String createQuizzesTable = 
            "CREATE TABLE IF NOT EXISTS quizzes (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "title VARCHAR(100) NOT NULL, " +
            "class_id INT NOT NULL, " +
            "due_date DATETIME NOT NULL, " +
            "file_path VARCHAR(255), " +
            "FOREIGN KEY (class_id) REFERENCES classes(id))";
            
        // Assignments table
        String createAssignmentsTable = 
            "CREATE TABLE IF NOT EXISTS assignments (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "title VARCHAR(100) NOT NULL, " +
            "class_id INT NOT NULL, " +
            "due_date DATETIME NOT NULL, " +
            "file_path VARCHAR(255), " +
            "FOREIGN KEY (class_id) REFERENCES classes(id))";
            
        // Assignment submissions table
        String createSubmissionsTable = 
            "CREATE TABLE IF NOT EXISTS submissions (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "assignment_id INT NOT NULL, " +
            "student_id INT NOT NULL, " +
            "submission_date DATETIME NOT NULL, " +
            "file_path VARCHAR(255) NOT NULL, " +
            "grade VARCHAR(10), " +
            "FOREIGN KEY (assignment_id) REFERENCES assignments(id), " +
            "FOREIGN KEY (student_id) REFERENCES users(id))";
            
        // Quiz attempts table
        String createAttemptsTable = 
            "CREATE TABLE IF NOT EXISTS quiz_attempts (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "quiz_id INT NOT NULL, " +
            "student_id INT NOT NULL, " +
            "attempt_date DATETIME NOT NULL, " +
            "score INT, " +
            "FOREIGN KEY (quiz_id) REFERENCES quizzes(id), " +
            "FOREIGN KEY (student_id) REFERENCES users(id))";
        
        try (var stmt = conn.createStatement()) {
            // Create tables
            stmt.execute(createUsersTable);
            stmt.execute(createClassesTable);
            stmt.execute(createEnrollmentsTable);
            stmt.execute(createQuizzesTable);
            stmt.execute(createAssignmentsTable);
            stmt.execute(createSubmissionsTable);
            stmt.execute(createAttemptsTable);
            
            // Check if we need to insert demo users
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                // Add demo users (teacher and student)
                stmt.execute("INSERT INTO users (username, password, role) VALUES " +
                             "('teacher', 'password123', 'teacher'), " +
                             "('student', 'password123', 'student')");
            }
        }
    }
}