package ui;

import db.DBConnection;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Virtual Classroom - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label (on the page)
        JLabel pageTitleLabel = new JLabel("Virtual Classroom Platform", SwingConstants.CENTER);
        pageTitleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Bold and larger font
        pageTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Add padding below the title
        mainPanel.add(pageTitleLabel, BorderLayout.NORTH); // Add the title label to the top of the page

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel()); // Empty cell for layout
        formPanel.add(new JLabel()); // Empty cell for layout

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this, 
                        "Please enter both username and password", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    User user = authenticate(username, password);
                    if (user != null) {
                        openDashboard(user);
                    } else {
                        JOptionPane.showMessageDialog(LoginPage.this, 
                            "Invalid username or password", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginPage.this, 
                        "Database error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        buttonPanel.add(loginButton);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private User authenticate(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Could not establish database connection");
            }
            
            String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                return new User(id, username, role);
            }
            return null;
        } finally {
            // Close resources in reverse order of creation
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    private void openDashboard(User user) {
        dispose(); // Close login window

        if ("teacher".equalsIgnoreCase(user.getRole())) {
            new TeacherDashboard(user);
        } else if ("student".equalsIgnoreCase(user.getRole())) {
            new StudentDashboard(user);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Unknown user role: " + user.getRole(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
        });
    }
}