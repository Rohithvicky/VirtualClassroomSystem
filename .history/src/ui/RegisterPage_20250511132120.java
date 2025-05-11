package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

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

    // Main panel with enhanced gradient background
    JPanel background = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180),
            0, getHeight(), new Color(138, 43, 226));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    background.setLayout(new BorderLayout());

    // Card panel for form with improved shadow effect
    JPanel cardPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create shadow effect
        int shadowSize = 10;
        for (int i = 0; i < shadowSize; i++) {
          float opacity = 0.2f * (1 - (float) i / shadowSize);
          g2.setColor(new Color(0, 0, 0, opacity));
          g2.fillRoundRect(
              shadowSize - i,
              shadowSize - i,
              getWidth() - (shadowSize - i) * 2,
              getHeight() - (shadowSize - i) * 2,
              20, 20);
        }

        // Draw card background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 20, 20);
      }
    };
    cardPanel.setLayout(new GridBagLayout());
    cardPanel.setOpaque(false);
    cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    // Add some padding around the card
    JPanel cardWrapper = new JPanel(new BorderLayout());
    cardWrapper.setOpaque(false);
    cardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    cardWrapper.add(cardPanel);

    // Form panel with proper spacing
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    // Logo or Icon (optional)
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;

    // Use text placeholder instead of trying to load an image
    JLabel logoLabel = new JLabel("Quick Mart");
    logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    logoLabel.setForeground(new Color(80, 80, 180));
    formPanel.add(logoLabel, gbc);

    // Title
    gbc.gridy++;
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
    JLabel usernameLabel = new JLabel("Username");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    usernameLabel.setForeground(new Color(80, 80, 80));
    formPanel.add(usernameLabel, gbc);

    // Username Field with icon
    gbc.gridy++;
    JPanel usernamePanel = new JPanel(new BorderLayout());
    usernamePanel.setOpaque(false);

    usernameField = new JTextField();
    styleTextField(usernameField);

    // Create a user icon label (you can replace with an actual icon)
    JLabel userIconLabel = new JLabel("👤");
    userIconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
    userIconLabel.setForeground(new Color(120, 120, 120));

    usernamePanel.add(userIconLabel, BorderLayout.WEST);
    usernamePanel.add(usernameField, BorderLayout.CENTER);
    formPanel.add(usernamePanel, gbc);

    // Password Label
    gbc.gridy++;
    JLabel passwordLabel = new JLabel("Password");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    passwordLabel.setForeground(new Color(80, 80, 80));
    formPanel.add(passwordLabel, gbc);

    // Password Field with icon
    gbc.gridy++;
    JPanel passwordPanel = new JPanel(new BorderLayout());
    passwordPanel.setOpaque(false);

    passwordField = new JPasswordField();
    styleTextField(passwordField);

    // Create a lock icon label (you can replace with an actual icon)
    JLabel lockIconLabel = new JLabel("🔒");
    lockIconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lockIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
    lockIconLabel.setForeground(new Color(120, 120, 120));

    passwordPanel.add(lockIconLabel, BorderLayout.WEST);
    passwordPanel.add(passwordField, BorderLayout.CENTER);
    formPanel.add(passwordPanel, gbc);

    // Show Password Checkbox
    gbc.gridy++;
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck.setOpaque(false);
    showPasswordCheck.setForeground(new Color(80, 80, 80));
    showPasswordCheck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    showPasswordCheck.setFocusPainted(false);
    showPasswordCheck.addActionListener(e -> {
      passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
    });
    formPanel.add(showPasswordCheck, gbc);

    // Register Button
    gbc.gridy++;
    gbc.insets = new Insets(25, 10, 10, 10);
    JButton registerButton = new JButton("REGISTER") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180),
            getWidth(), getHeight(), new Color(100, 100, 200));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
      }
    };
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setForeground(Color.WHITE);
    registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    registerButton.setFocusPainted(false);
    registerButton.setContentAreaFilled(false);
    registerButton.setBorderPainted(false);
    registerButton.setOpaque(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    registerButton.addActionListener(e -> registerUser());

    // Button hover effects
    registerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        registerButton.setForeground(new Color(255, 255, 220));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        registerButton.setForeground(Color.WHITE);
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

    // Fixed login link that won't shake
    JLabel loginLink = new JLabel("Login here");
    loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loginLink.setForeground(new Color(70, 130, 180));
    loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Better mouse event handling
    loginLink.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginPage());
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        loginLink.setForeground(new Color(100, 100, 200));
        // Use setUnderlined instead of changing the whole text which causes "shaking"
        Font font = loginLink.getFont();
        // Create a new HashMap instead of using the direct result of getAttributes()
        HashMap<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        loginLink.setFont(font.deriveFont(attributes));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        loginLink.setForeground(new Color(70, 130, 180));
        // Remove the underline
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
      }
    });

    loginPanel.add(loginPrompt);
    loginPanel.add(loginLink);
    formPanel.add(loginPanel, gbc);

    cardPanel.add(formPanel);
    background.add(cardWrapper, BorderLayout.CENTER);
    setContentPane(background);
    setVisible(true);
  }

  private void styleTextField(JTextField field) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    field.setPreferredSize(new Dimension(250, 40));
    field.setOpaque(false);
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