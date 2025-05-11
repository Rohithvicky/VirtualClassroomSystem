package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class RegisterPage extends JFrame {
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JCheckBox showPasswordCheck;

  public RegisterPage() {
    setTitle("Virtual Classroom - Register");
    setSize(400, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(true);

    // Main panel with simple BorderLayout and padding
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title Panel at the top
    JPanel titlePanel = new JPanel();
    JLabel titleLabel = new JLabel("Register");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titlePanel.add(titleLabel);
    mainPanel.add(titlePanel, BorderLayout.NORTH);

    // Form Panel in the center using GridLayout for simplicity
    JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    // Username label & text field
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(usernameLabel);
    usernameField = new JTextField();
    usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(usernameField);

    // Password label & password field
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(passwordLabel);
    passwordField = new JPasswordField();
    passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(passwordField);

    // Checkbox to show/hide password
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck
        .addActionListener(e -> passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•'));
    formPanel.add(showPasswordCheck);

    // Register button
    JButton registerButton = new JButton("Create Account");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(41, 128, 185));
    registerButton.setForeground(Color.WHITE);
    registerButton.setFocusPainted(false);
    registerButton.addActionListener(e -> registerUser());
    formPanel.add(registerButton);

    // Login section at the bottom
    JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel alreadyLabel = new JLabel("Already have an account?");
    alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    loginPanel.add(alreadyLabel);
    JButton loginButton = new JButton("Login");
    loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    loginButton.setForeground(new Color(41, 128, 185));
    loginButton.setBackground(Color.WHITE);
    loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    loginButton.addActionListener(e -> openLoginPage());
    loginPanel.add(loginButton);

    // Assemble panels into main panel
    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(loginPanel, BorderLayout.SOUTH);
    setContentPane(mainPanel);
    setVisible(true);
  }

  // Method to register a new user
  private void registerUser() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    // Basic validation checks
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
      // Check if username already exists
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

  // Check if the username exists in the database
  private boolean checkUsernameExists(Connection conn, String username) throws SQLException {
    String sql = "SELECT 1 FROM users WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  // Open the login page
  private void openLoginPage() {
    dispose();
    SwingUtilities.invokeLater(() -> {
      LoginPage loginPage = new LoginPage();
      loginPage.setVisible(true);
    });
  }

  // Main method to run the RegisterPage
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