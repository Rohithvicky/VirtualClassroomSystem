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
    setSize(500, 650);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(true);

    // Main panel with a subtle gradient background for a professional look
    JPanel background = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(Graphics2D.KEY_RENDERING, Graphics2D.VALUE_RENDER_QUALITY);
        Color startColor = new Color(240, 240, 245);
        Color endColor = new Color(225, 225, 235);
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
      }
    };
    background.setLayout(new BorderLayout());

    // Card panel for form with a drop shadow effect for professionalism
    JPanel cardPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(Graphics2D.KEY_ANTIALIASING, Graphics2D.VALUE_ANTIALIAS_ON);
        // Shadow effect
        int shadowSize = 8;
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize, getHeight() - shadowSize, 20, 20);
        // Card background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, 20, 20);
      }
    };
    cardPanel.setLayout(new GridBagLayout());
    cardPanel.setOpaque(false);
    cardPanel.setPreferredSize(new Dimension(400, 550));
    cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    // Form panel aligning items properly
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    // Logo/Title
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JLabel logoLabel = new JLabel("Virtual Classroom");
    logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    logoLabel.setForeground(new Color(44, 62, 80));
    formPanel.add(logoLabel, gbc);

    // Title
    gbc.gridy++;
    JLabel titleLabel = new JLabel("Create Your Account");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titleLabel.setForeground(new Color(44, 62, 80));
    formPanel.add(titleLabel, gbc);

    // Subtitle
    gbc.gridy++;
    JLabel subtitleLabel = new JLabel("Join our virtual learning community");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitleLabel.setForeground(new Color(100, 100, 110));
    formPanel.add(subtitleLabel, gbc);

    // Spacer
    gbc.gridy++;
    formPanel.add(Box.createVerticalStrut(30), gbc);

    // Username Label
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.LINE_START;
    JLabel usernameLabel = new JLabel("Username");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    usernameLabel.setForeground(new Color(44, 62, 80));
    formPanel.add(usernameLabel, gbc);

    // Username Field
    gbc.gridy++;
    usernameField = new JTextField();
    styleTextField(usernameField);
    formPanel.add(usernameField, gbc);

    // Password Label
    gbc.gridy++;
    JLabel passwordLabel = new JLabel("Password");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    passwordLabel.setForeground(new Color(44, 62, 80));
    formPanel.add(passwordLabel, gbc);

    // Password Field
    gbc.gridy++;
    passwordField = new JPasswordField();
    styleTextField(passwordField);
    formPanel.add(passwordField, gbc);

    // Show Password Checkbox with professional styling
    gbc.gridy++;
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck.setOpaque(false);
    showPasswordCheck.setForeground(new Color(100, 100, 110));
    showPasswordCheck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    showPasswordCheck.setFocusPainted(false);
    showPasswordCheck
        .addActionListener(e -> passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•'));
    formPanel.add(showPasswordCheck, gbc);

    // Register Button with professional color scheme
    gbc.gridy++;
    gbc.insets = new Insets(25, 10, 10, 10);
    JButton registerButton = new JButton("CREATE ACCOUNT");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(41, 128, 185));
    registerButton.setForeground(Color.WHITE);
    registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    registerButton.setFocusPainted(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    registerButton.addActionListener(e -> registerUser());
    registerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        registerButton.setBackground(new Color(31, 97, 141));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        registerButton.setBackground(new Color(41, 128, 185));
      }
    });
    formPanel.add(registerButton, gbc);

    // Login Button for direct access for existing users
    gbc.gridy++;
    gbc.insets = new Insets(10, 10, 10, 10);
    JButton loginButton = new JButton("LOGIN");
    loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    loginButton.setBackground(Color.WHITE);
    loginButton.setForeground(new Color(41, 128, 185));
    loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginButton.setFocusPainted(false);
    loginButton.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
    loginButton.addActionListener(e -> openLoginPage());
    loginButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        loginButton.setBackground(new Color(245, 245, 245));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        loginButton.setBackground(Color.WHITE);
      }
    });
    formPanel.add(loginButton, gbc);

    // Divider for visual separation
    gbc.gridy++;
    gbc.insets = new Insets(20, 10, 10, 10);
    JSeparator separator = new JSeparator();
    separator.setForeground(new Color(220, 220, 220));
    formPanel.add(separator, gbc);

    // Additional login prompt with clickable link
    gbc.gridy++;
    gbc.insets = new Insets(10, 10, 0, 10);
    JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    loginPanel.setOpaque(false);
    JLabel loginPrompt = new JLabel("Already have an account?");
    loginPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginPrompt.setForeground(new Color(100, 100, 110));
    JLabel loginLink = new JLabel("Sign in");
    loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loginLink.setForeground(new Color(41, 128, 185));
    loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginLink.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        openLoginPage();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        Font font = loginLink.getFont();
        HashMap<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        loginLink.setFont(font.deriveFont(attributes));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
      }
    });
    loginPanel.add(loginPrompt);
    loginPanel.add(loginLink);
    formPanel.add(loginPanel, gbc);

    cardPanel.add(formPanel);
    // Center the card panel
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setOpaque(false);
    centerPanel.add(cardPanel);
    background.add(centerPanel, BorderLayout.CENTER);
    setContentPane(background);
    setVisible(true);
  }

  private void styleTextField(JTextField field) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    field.setPreferredSize(new Dimension(300, 45));
    field.setBackground(new Color(250, 250, 250));
  }

  private void registerUser() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    // Basic validation
    if (username.isEmpty() || password.isEmpty()) {
      showError("Please fill in all fields.");
      return;
    }
    if (username.length() < 4) {
      showError("Username must be at least 4 characters long.");
      return;
    }
    if (password.length() < 8) {
      showError("Password must be at least 8 characters long.");
      return;
    }

    try (Connection conn = DBConnection.getConnection()) {
      // Check if the username already exists
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
          JOptionPane.showMessageDialog(this,
              "Registration successful! You can now login.",
              "Success",
              JOptionPane.INFORMATION_MESSAGE);
          openLoginPage();
        } else {
          showError("Registration failed. Please try again.");
        }
      }
    } catch (SQLException e) {
      showError("Database error: " + e.getMessage());
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

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // Ensure modern UI defaults for a professional look
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
      } catch (Exception e) {
        e.printStackTrace();
      }
      new RegisterPage();
    });
  }
}