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

        // Launch Register Page instead of Login Page
        SwingUtilities.invokeLater(() -> {
            RegisterPage registerPage = new RegisterPage();
            registerPage.setVisible(true);
        });
    }
}
