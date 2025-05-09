package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class LiveClassPanel extends JPanel {

    public LiveClassPanel() {
        setLayout(new GridLayout(0, 1));

        JLabel title = new JLabel("Join a Live Class");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title);

        // Sample hardcoded links
        addLiveClassLink("Math Class", "https://meet.google.com/math-class");
        addLiveClassLink("Science Class", "https://meet.google.com/science-class");
        addLiveClassLink("Programming Class", "https://meet.google.com/programming-class");
    }

    private void addLiveClassLink(String className, String url) {
        JButton linkButton = new JButton("Join " + className);
        linkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to open link.");
                    ex.printStackTrace();
                }
            }
        });
        add(linkButton);
    }
}
