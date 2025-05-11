package ui;

import javax.swing.*;

public class VirtualClassroomApp {
    public static void main(String[] args) {
        // Set a modern-looking UI theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Failed to set look and feel.");
        }

        // Launch Login Page
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);  // This line was missing
        });
    }
}

