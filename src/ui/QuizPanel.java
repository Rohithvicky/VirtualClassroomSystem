package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class QuizPanel extends JPanel {
    public QuizPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        loadQuizzes();
    }

    private void loadQuizzes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM quizzes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JPanel questionPanel = new JPanel(new GridLayout(0, 1));
                questionPanel.setBorder(BorderFactory.createTitledBorder("Question"));

                JLabel questionLabel = new JLabel(rs.getString("question"));
                JRadioButton a = new JRadioButton("A. " + rs.getString("option_a"));
                JRadioButton b = new JRadioButton("B. " + rs.getString("option_b"));
                JRadioButton c = new JRadioButton("C. " + rs.getString("option_c"));
                JRadioButton d = new JRadioButton("D. " + rs.getString("option_d"));

                ButtonGroup group = new ButtonGroup();
                group.add(a); group.add(b); group.add(c); group.add(d);

                questionPanel.add(questionLabel);
                questionPanel.add(a);
                questionPanel.add(b);
                questionPanel.add(c);
                questionPanel.add(d);

                add(questionPanel);
            }
        } catch (SQLException e) {
            add(new JLabel("Failed to load quizzes"));
            e.printStackTrace();
        }
    }
}
