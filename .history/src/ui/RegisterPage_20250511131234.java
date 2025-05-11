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
    setSize(450, 550);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    // Main panel with gradient background
    JPanel background = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(72, 177, 191),
            0, getHeight(), new Color(235, 129, 202));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    background.setLayout(new BorderLayout());

    // Card panel for form
    JPanel cardPanel = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g);
      }
    };
    cardPanel.setOpaque(false);
    cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
    cardPanel.setPreferredSize(new Dimension(400, 500));

    // Form panel with proper spacing
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    // Title
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JLabel titleLabel = new JLabel("Create Account");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titleLabel.setForeground(new Color(50, 50, 50));
    formPanel.add(titleLabel, gbc);

    // Subtitle
    gbc.gridy++;
    JLabel subtitleLabel = new JLabel("Fill in your details to register");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(100, 100, 100));
    formPanel.add(subtitleLabel, gbc);

    // Spacer
    gbc.gridy++;
    formPanel.add(Box.createVerticalStrut(20), gbc);

    // Username Label
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.LINE_START;
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    usernameLabel.setForeground(new Color(80, 80, 80));
    formPanel.add(usernameLabel, gbc);

    // Username Field
    gbc.gridy++;
    usernameField = new JTextField();
    styleTextField(usernameField);
    formPanel.add(usernameField, gbc);

    // Password Label
    gbc.gridy++;
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    passwordLabel.setForeground(new Color(80, 80, 80));
    formPanel.add(passwordLabel, gbc);

    // Password Field
    gbc.gridy++;
    passwordField = new JPasswordField();
    styleTextField(passwordField);
    formPanel.add(passwordField, gbc);

    // Show Password Checkbox
    gbc.gridy++;
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck.setOpaque(false);
    showPasswordCheck.setForeground(new Color(80, 80, 80));
    showPasswordCheck.addActionListener(e -> {
      passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
    });
    formPanel.add(showPasswordCheck, gbc);

    // Register Button
    gbc.gridy++;
    gbc.insets = new Insets(20, 10, 10, 10);
    JButton registerButton = new JButton("REGISTER");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(0, 123, 255));
    registerButton.setForeground(Color.WHITE);
    registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    registerButton.setFocusPainted(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    registerButton.addActionListener(e -> registerUser());

    // Button hover effects
    registerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        registerButton.setBackground(new Color(0, 105, 217));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        registerButton.setBackground(new Color(0, 123, 255));
      }
    });

    formPanel.add(registerButton, gbc);

    // Login prompt
    gbc.gridy++;
    gbc.insets = new Insets(20, 10, 0, 10);
    JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    loginPanel.setOpaque(false);
    JLabel loginPrompt = new JLabel("Already have an account?");
    loginPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginPrompt.setForeground(new Color(100, 100, 100));

    JLabel loginLink = new JLabel("Login here");
    loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loginLink.setForeground(new Color(0, 123, 255));
    loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginLink.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        dispose();
        new LoginPage();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        loginLink.setText("<html><u>Login here</u></html>");
      }

      @Override
      public void mouseExited(MouseEvent e) {
        loginLink.setText("Login here");
      }
    });

    loginPanel.add(loginPrompt);
    loginPanel.add(loginLink);
    formPanel.add(loginPanel, gbc);

    cardPanel.add(formPanel);
    background.add(cardPanel, BorderLayout.CENTER);
    setContentPane(background);
    setVisible(true);
  }

  private void styleTextField(JTextField field) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    field.setPreferredSize(new Dimension(250, 40));
  }

  private void registerUser() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    // Validation
    if (username.isEmpty() || password.isEmpty()) {
      showError("Please fill in all fields.");
      return;
    }

    if (username.length() < 4) {
      showError("Username must be at least 4 characters long.");
      return;
    }

    if (password.length() < 6) {
      showError("Password must be at least 6 characters long.");
      return;
    }

    try (Connection conn = DBConnection.getConnection()) {
      String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, username);
      stmt.setString(2, password);

      int rows = stmt.executeUpdate();
      if (rows > 0) {
        JOptionPane.showMessageDialog(this,
            "Registration successful!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginPage();
      } else {
        showError("Registration failed. Please try again.");
      }
    } catch (SQLException e) {
      if (e.getMessage().contains("duplicate")) {
        showError("Username already exists. Please choose another.");
      } else {
        showError("Database error: " + e.getMessage());
      }
      e.printStackTrace();
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this,
        message,
        "Error",
        JOptionPane.ERROR_MESSAGE);
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