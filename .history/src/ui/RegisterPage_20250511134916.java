package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterPage extends JFrame {
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JCheckBox showPasswordCheck;

  public RegisterPage() {
    setTitle("Virtual Classroom - Register");
    setSize(500, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(true);

    // Main panel using BorderLayout with padding
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header Panel with the exact text from extraction
    String headerText = "<html>" +
        "<div style='text-align: center; font-family: \"Segoe UI\", sans-serif;'>" +
        "<h2 style='margin: 2px; color: #2C3E50;'>Aurora Deemed To Be University</h2>" +
        "<p style='margin: 2px; font-size: 14px; color: #34495E;'>School of Engineering<br>" +
        "Department Of Computer Science<br>" +
        "2nd Year & Term-3<br>" +
        "Course: Java Programming</p>" +
        "<p style='margin: 2px; font-size: 14px; color: #34495E;'>" +
        "Project Title: Virtual Classroom Platform</p>" +
        "<p style='margin: 2px; font-size: 14px; color: #34495E;'>" +
        "Course Instructor:<br>" +
        "Ms. K. Jayasri<br>" +
        "Assistant Professor<br>" +
        "Department Of Computer Science</p>" +
        "<p style='margin: 2px; font-size: 14px; color: #34495E;'>" +
        "Presented By:<br>" +
        "LG - 7<br>" +
        "S. Ganesh (23U1R1048)<br>" +
        "B. Shreeya (231U1R1050)<br>" +
        "T. Vignesh (231U1R1053)<br>" +
        "B. Shruthika (241U1L1001)</p>" +
        "</div></html>";
    JLabel headerLabel = new JLabel(headerText);
    headerLabel.setOpaque(true);
    headerLabel.setBackground(new Color(230, 230, 250)); // light lavender background
    headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    mainPanel.add(headerLabel, BorderLayout.NORTH);

    // Form panel using GridLayout
    JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // "Register" Title
    JLabel formTitle = new JLabel("Register Your Account", SwingConstants.CENTER);
    formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    formTitle.setForeground(new Color(41, 128, 185));
    formPanel.add(formTitle);

    // Username Field
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(usernameLabel);
    usernameField = new JTextField();
    usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(usernameField);

    // Password Field
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(passwordLabel);
    passwordField = new JPasswordField();
    passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(passwordField);

    // Show Password Checkbox
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck
        .addActionListener(e -> passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•'));
    formPanel.add(showPasswordCheck);

    // Register Button
    JButton registerButton = new JButton("Create Account");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(41, 128, 185));
    registerButton.setForeground(Color.WHITE);
    registerButton.setFocusPainted(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    registerButton.addActionListener(e -> registerUser());
    formPanel.add(registerButton);

    // Login Panel
    JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel loginPrompt = new JLabel("Already have an account?");
    loginPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    loginPanel.add(loginPrompt);
    JButton loginButton = new JButton("Login");
    loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    loginButton.setBackground(Color.WHITE);
    loginButton.setForeground(new Color(41, 128, 185));
    loginButton.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
    loginButton.addActionListener(e -> openLoginPage());
    loginPanel.add(loginButton);

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(loginPanel, BorderLayout.SOUTH);

    setContentPane(mainPanel);
    setVisible(true);
  }

  private void registerUser() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (username.length() < 4) {
      JOptionPane.showMessageDialog(this, "Username must be at least 4 characters long.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (password.length() < 8) {
      JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try (Connection conn = DBConnection.getConnection()) {
      if (checkUsernameExists(conn, username)) {
        int option = JOptionPane.showConfirmDialog(this,
            "Username already exists. Would you like to login instead?",
            "Account Exists",
            JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
          openLoginPage();
        }
        return;
      }
      String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, password);
        int rows = stmt.executeUpdate();
        if (rows > 0) {
          JOptionPane.showMessageDialog(this, "Registration successful! You can now login.",
              "Success", JOptionPane.INFORMATION_MESSAGE);
          openLoginPage();
        } else {
          JOptionPane.showMessageDialog(this, "Registration failed. Please try again.",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  private boolean checkUsernameExists(Connection conn, String username) throws SQLException {
    String sql = "SELECT 1 FROM users WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  private void openLoginPage() {
    dispose();
    SwingUtilities.invokeLater(() -> {
      LoginPage loginPage = new LoginPage();
      loginPage.setVisible(true);
    });
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      new RegisterPage();
    });
  }
}