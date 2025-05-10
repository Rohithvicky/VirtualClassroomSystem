package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import db.DBConnection;
import javax.swing.JOptionPane;

/**
 * This class provides a fixed implementation for the attendance functionality.
 * It resolves the "Unknown column 'student_id' in 'field list'" error by
 * ensuring all queries use the correct column name 'user_id'.
 */
public class AttendanceManager {

    /**
     * Records attendance for a user in a class
     * 
     * @param classId The ID of the class
     * @param userId The ID of the user attending the class
     * @return true if attendance is successfully recorded, false otherwise
     */
    public static boolean markAttendance(int classId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            // First, verify that the class exists
            String checkClassSql = "SELECT id FROM classes WHERE id = ?";
            stmt = conn.prepareStatement(checkClassSql);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, 
                    "Error: Class ID " + classId + " not found in the database.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            rs.close();
            stmt.close();
            
            // Next, verify that the user exists
            String checkUserSql = "SELECT id FROM users WHERE id = ?";
            stmt = conn.prepareStatement(checkUserSql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, 
                    "Error: User ID " + userId + " not found in the database.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            rs.close();
            stmt.close();
            
            // Check if attendance already exists for today
            String checkAttendanceSql = "SELECT id FROM attendance WHERE class_id = ? AND user_id = ? AND attendance_date = CURRENT_DATE()";
            stmt = conn.prepareStatement(checkAttendanceSql);
            stmt.setInt(1, classId);
            stmt.setInt(2, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, 
                    "Attendance already marked for today!", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return true; // Already marked, consider it a success
            }
            
            rs.close();
            stmt.close();
            
            // Finally, insert the attendance record using user_id
            String insertSql = "INSERT INTO attendance (class_id, user_id, attendance_date) VALUES (?, ?, CURRENT_DATE())";
            stmt = conn.prepareStatement(insertSql);
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
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the class ID from a class title displayed in the UI
     * 
     * @param displayTitle The formatted title string shown in the UI list
     * @return The database ID of the class, or -1 if not found
     */
    public static int getClassIdFromDisplayTitle(String displayTitle) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Parse the display title to extract actual class title
            // Format is usually "Title - Meet: link"
            String classTitle = displayTitle;
            if (displayTitle.contains(" - Meet:")) {
                classTitle = displayTitle.split(" - Meet:")[0].trim();
            }
            
            conn = DBConnection.getConnection();
            String sql = "SELECT id FROM classes WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, classTitle);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database error when getting class ID: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return -1; // Class not found
    }
}