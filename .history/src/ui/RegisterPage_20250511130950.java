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
    setLocationRelativeTo(null); // Center window
    setResizable(false);

    // Gradient background panel
    JPanel background = new JPanel() {
      @Override
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

    // Card-like white form panel with rounded corners using GridBagLayout
    JPanel formPanel = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
      }
    };
    formPanel.setOpaque(false);
    formPanel.setPreferredSize(new Dimension(350, 450));
    formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 5, 10, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;

    // Title
    JLabel titleLabel = new JLabel("Create Account");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
    formPanel.add(titleLabel, gbc);

    // Username Label
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(usernameLabel, gbc);

    // Username Field
    gbc.gridx = 1;
    usernameField = new JTextField(15);
    styleTextField(usernameField);
    formPanel.add(usernameField, gbc);

    // Password Label
    gbc.gridx = 0;
    gbc.gridy++;
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    formPanel.add(passwordLabel, gbc);

    // Password Field
    gbc.gridx = 1;
    passwordField = new JPasswordField(15);
    styleTextField(passwordField);
    formPanel.add(passwordField, gbc);

    // Show Password Checkbox
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    showPasswordCheck = new JCheckBox("Show Password");
    showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    showPasswordCheck.setOpaque(false);
    showPasswordCheck.addActionListener(e -> passwordField.setEchoChar(
        showPasswordCheck.isSelected() ? (char) 0 : '•'));
    formPanel.add(showPasswordCheck, gbc);

    // Register Button
    gbc.gridy++;
    JButton registerButton = new JButton("REGISTER");
    registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    registerButton.setBackground(new Color(0, 123, 255));
    registerButton.setForeground(Color.WHITE);
    registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    registerButton.setFocusPainted(false);
    registerButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    registerButton.addActionListener(e -> registerUser());
    formPanel.add(registerButton, gbc);

    // Login Prompt Label
    gbc.gridy++;
    JLabel loginPrompt = new JLabel("Already have an account?");
    loginPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginPrompt.setHorizontalAlignment(SwingConstants.CENTER);
    formPanel.add(loginPrompt, gbc);

    // Login Hyperlink label
    gbc.gridy++;
    JLabel loginHere = new JLabel("LOGIN HERE");
    loginHere.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loginHere.setForeground(Color.BLUE.darker());
    loginHere.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    loginHere.setHorizontalAlignment(SwingConstants.CENTER);
    loginHere.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        dispose();
        new LoginPage();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        loginHere.setText("<html><u>LOGIN HERE</u></html>");
      }

      @Override
      public void mouseExited(MouseEvent e) {
        loginHere.setText("LOGIN HERE");
      }
    });
    formPanel.add(loginHere, gbc);

    background.add(formPanel);
    setContentPane(background);
    setVisible(true);
  }

  private void styleTextField(JTextField field) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
    SwingUtilities.invokeLater(RegisterPage::new);
  }
}