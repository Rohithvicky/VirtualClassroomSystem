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
    setSize(400, 450);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window

    // Gradient background
    JPanel background = new JPanel() {
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(99, 179, 237),
            0, getHeight(), new Color(220, 91, 183));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    background.setLayout(new GridBagLayout());

    JPanel formPanel = new JPanel();
    formPanel.setPreferredSize(new Dimension(300, 350));
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBackground(Color.WHITE);
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    formPanel.add(titleLabel);

    formPanel.add(Box.createVerticalStrut(20));

    JLabel usernameLabel = new JLabel("Username");
    usernameField = new JTextField();
    formPanel.add(usernameLabel);
    formPanel.add(usernameField);

    formPanel.add(Box.createVerticalStrut(10));

    JLabel passwordLabel = new JLabel("Password");
    passwordField = new JPasswordField();
    formPanel.add(passwordLabel);
    formPanel.add(passwordField);

    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.addActionListener(e -> {
      passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
    });
    formPanel.add(showPasswordCheck);

    formPanel.add(Box.createVerticalStrut(15));

    JButton registerButton = new JButton("REGISTER");
    registerButton.setBackground(new Color(255, 99, 99));
    registerButton.setForeground(Color.WHITE);
    registerButton.setFocusPainted(false);
    registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    registerButton.addActionListener(e -> registerUser());

    formPanel.add(registerButton);

    formPanel.add(Box.createVerticalStrut(20));

    JLabel loginLabel = new JLabel("Already have an account?");
    loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    JLabel loginHere = new JLabel("LOGIN HERE");
    loginHere.setForeground(Color.BLUE.darker());
    loginHere.setAlignmentX(Component.CENTER_ALIGNMENT);
    loginHere.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginHere.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        dispose();
        new LoginPage();
      }
    });

    formPanel.add(loginLabel);
    formPanel.add(loginHere);

    background.add(formPanel);
    setContentPane(background);
    setVisible(true);
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
