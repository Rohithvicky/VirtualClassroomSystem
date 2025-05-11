package ui;

import db.DBConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import model.User;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Virtual Classroom - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Allow resizing with a minimum size set
        setResizable(true);
        setMinimumSize(new Dimension(800, 600));

        // Main panel with BorderLayout and padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel: University & Project Info (same style as RegisterPage)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(230, 230, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel universityLabel = new JLabel("Aurora Deemed To Be University", SwingConstants.CENTER);
        universityLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        universityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel schoolLabel = new JLabel("School of Engineering", SwingConstants.CENTER);
        schoolLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        schoolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel deptLabel = new JLabel("Department Of Computer Science", SwingConstants.CENTER);
        deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel yearLabel = new JLabel("2nd Year & Term-3", SwingConstants.CENTER);
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel courseLabel = new JLabel("Course: Java Programming", SwingConstants.CENTER);
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel projectLabel = new JLabel("Project Title: Virtual Classroom Platform", SwingConstants.CENTER);
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        projectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(universityLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(schoolLabel);
        headerPanel.add(deptLabel);
        headerPanel.add(yearLabel);
        headerPanel.add(courseLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(projectLabel);

        // Form Panel using GridBagLayout for a clean, aligned look
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // "Login to Your Account" Title across two columns
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel formTitle = new JLabel("Login to Your Account", SwingConstants.CENTER);
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(new Color(41, 128, 185));
        formPanel.add(formTitle, gbc);

        // Reset gridwidth and next row
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Username Label
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        // Username Field directly aside the label
        gbc.gridx = 1;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // Next row: Password Label
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        // Password Field directly aside the label
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Row for Login button spanning two columns with full blue background
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPage.this,
                        "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                User user = authenticate(username, password);
                if (user != null) {
                    openDashboard(user);
                } else {
                    // Debug output to console (remove in production)
                    System.out.println("DEBUG: Login failed for user: " + username);
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginPage.this,
                        "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        formPanel.add(loginButton, gbc);

        // Footer Panel (reuse layout from RegisterPage for consistency)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // "Don't have an account? Register" link/button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel registerPrompt = new JLabel("Don't have an account?");
        registerPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(new Color(41, 128, 185));
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterPage();
        });
        actionPanel.add(registerPrompt);
        actionPanel.add(registerButton);
        footerPanel.add(actionPanel, BorderLayout.CENTER);

        // Assemble all panels into the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
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

            // Debug output: display query details in console
            System.out.println("DEBUG: Executing query with username: " + username);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                // Debug output: user authenticated
                System.out.println("DEBUG: User authenticated: " + username + ", role: " + role);
                return new User(id, username, role);
            }
            return null;
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
    }

    private void openDashboard(User user) {
        dispose();
        if ("teacher".equalsIgnoreCase(user.getRole())) {
            new TeacherDashboard(user);
        } else if ("student".equalsIgnoreCase(user.getRole())) {
            new StudentDashboard(user);
        } else {
            JOptionPane.showMessageDialog(null, "Unknown user role: " + user.getRole(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}