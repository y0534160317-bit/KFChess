package org.kfchess.view.panels;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    private final JLabel scoreLabel;

    public HeaderPanel() {

        setLayout(new BorderLayout());

        scoreLabel = new JLabel("⚫ Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));

        add(scoreLabel, BorderLayout.WEST);
    }

    public void setScore(int score) {
        scoreLabel.setText("⚫ Score: " + score);
        revalidate();
        repaint();
    }
}