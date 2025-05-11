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
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    // Main panel using BorderLayout with padding
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header Panel - University & Project Info only
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

    // Login Panel at the top of Footer
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

    // Footer Panel combining Instructor info (left) and Presented By info (right)
    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBackground(new Color(245, 245, 245));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Instructor Panel (left side)
    JPanel instructorPanel = new JPanel();
    instructorPanel.setLayout(new BoxLayout(instructorPanel, BoxLayout.Y_AXIS));
    instructorPanel.setBackground(new Color(245, 245, 245));

    JLabel instructorTitle = new JLabel("Course Instructor:");
    instructorTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
    instructorTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel instructorName = new JLabel("Ms. K. Jayasri");
    instructorName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    instructorName.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel instructorPosition = new JLabel("Assistant Professor");
    instructorPosition.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    instructorPosition.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel instructorDept = new JLabel("Department Of Computer Science");
    instructorDept.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    instructorDept.setAlignmentX(Component.LEFT_ALIGNMENT);

    instructorPanel.add(instructorTitle);
    instructorPanel.add(Box.createVerticalStrut(5));
    instructorPanel.add(instructorName);
    instructorPanel.add(instructorPosition);
    instructorPanel.add(instructorDept);

    // Presented By Panel (right side)
    JPanel presentedByPanel = new JPanel();
    presentedByPanel.setLayout(new BoxLayout(presentedByPanel, BoxLayout.Y_AXIS));
    presentedByPanel.setBackground(new Color(245, 245, 245));
    presentedByPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

    JLabel presentedByLabel = new JLabel("Presented By:");
    presentedByLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    presentedByLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    JLabel groupLabel = new JLabel("LG - 7");
    groupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    groupLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    JLabel member1 = new JLabel("S. Ganesh (23U1R1048)");
    member1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    member1.setAlignmentX(Component.RIGHT_ALIGNMENT);
    JLabel member2 = new JLabel("B. Shreeya (231U1R1050)");
    member2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    member2.setAlignmentX(Component.RIGHT_ALIGNMENT);
    JLabel member3 = new JLabel("T. Vignesh (231U1R1053)");
    member3.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    member3.setAlignmentX(Component.RIGHT_ALIGNMENT);
    JLabel member4 = new JLabel("B. Shruthika (241U1L1001)");
    member4.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    member4.setAlignmentX(Component.RIGHT_ALIGNMENT);

    presentedByPanel.add(presentedByLabel);
    presentedByPanel.add(Box.createVerticalStrut(5));
    presentedByPanel.add(groupLabel);
    presentedByPanel.add(member1);
    presentedByPanel.add(member2);
    presentedByPanel.add(member3);
    presentedByPanel.add(member4);

    infoPanel.add(instructorPanel, BorderLayout.WEST);
    infoPanel.add(presentedByPanel, BorderLayout.EAST);

    // Combine Login Panel and Info Panel into the Footer
    JPanel footerPanel = new JPanel();
    footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
    footerPanel.add(loginPanel);
    footerPanel.add(Box.createVerticalStrut(10));
    footerPanel.add(infoPanel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(footerPanel, BorderLayout.SOUTH);

    setContentPane(mainPanel);
    pack(); // Automatically size the window based on components
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