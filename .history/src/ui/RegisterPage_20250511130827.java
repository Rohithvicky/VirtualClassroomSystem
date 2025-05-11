package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JFrame {
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JCheckBox showPasswordCheck;

  public RegisterPage() {
    setTitle("Quick Mart - Register");
    setSize(420, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center window
    setResizable(false);

    // Gradient background panel
    JPanel background = new JPanel() {
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(72, 177, 191),
            0, getHeight(), new Color(235, 129, 202));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    background.setLayout(new GridBagLayout());

    // Card-like white form panel with rounded corners
    JPanel formPanel = new JPanel() {
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
      }
    };
    formPanel.setOpaque(false);
    formPanel.setPreferredSize(new Dimension(330, 380));
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

    // Title
    JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    formPanel.add(titleLabel);
    formPanel.add(Box.createVerticalStrut(20));

    // Username
    JLabel usernameLabel = new JLabel("Username");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    usernameField = new JTextField();
    styleTextField(usernameField);
    formPanel.add(usernameLabel);
    formPanel.add(usernameField);
    formPanel.add(Box.createVerticalStrut(10));

    // Password
    JLabel passwordLabel = new JLabel("Password");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    passwordField = new JPasswordField();
    styleTextField(passwordField);
    formPanel.add(passwordLabel);
    formPanel.add(passwordField);

    // Show Password
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck.setOpaque(false);
    showPasswordCheck
        .addActionListener(e -> passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•'));
    formPanel.add(showPasswordCheck);
    formPanel.add(Box.createVerticalStrut(20));

    // Register button
    JButton registerButton = new JButton("REGISTER");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(0, 123, 255));
    registerButton.setForeground(Color.WHITE);
    registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    registerButton.setFocusPainted(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    registerButton.addActionListener(e -> registerUser());
    formPanel.add(registerButton);
    formPanel.add(Box.createVerticalStrut(25));

    // Login Redirect
    JLabel loginLabel = new JLabel("Already have an account?");
    loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    formPanel.add(loginLabel);

    JLabel loginHere = new JLabel("LOGIN HERE");
    loginHere.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loginHere.setForeground(Color.BLUE.darker());
    loginHere.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginHere.setAlignmentX(Component.CENTER_ALIGNMENT);
    loginHere.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        dispose();
        new LoginPage();
      }

      public void mouseEntered(MouseEvent e) {
        loginHere.setText("<html><u>LOGIN HERE</u></html>");
      }

      public void mouseExited(MouseEvent e) {
        loginHere.setText("LOGIN HERE");
      }
    });
    formPanel.add(loginHere);

    background.add(formPanel);
    setContentPane(background);
    setVisible(true);
  }

  private void styleTextField(JTextField field) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(180, 180, 180)),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
  }

  private void registerUser() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try (Connection conn = DBConnection.getConnection()) {
      String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, username);
      stmt.setString(2, password);

      int rows = stmt.executeUpdate();
      if (rows > 0) {
        JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginPage();
      } else {
        JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
      }

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new RegisterPage());
  }
}
