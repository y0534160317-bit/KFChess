/*)package org.kfchess.view.panels;

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
}*/

package org.kfchess.client.view.panels;

import org.kfchess.shared.events.EventBus;
import org.kfchess.shared.events.EventListener;
import org.kfchess.shared.events.ScoreChangedEvent;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel implements EventListener<ScoreChangedEvent> {

    private final JLabel playerLabel;
    private final JLabel scoreLabel;

    public HeaderPanel(EventBus eventBus) {
        setLayout(new BorderLayout());

        // צבע רקע שמתאים ללוח עץ
        setBackground(new Color(181,136,99));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,2,0,new Color(160,120,88)),
                BorderFactory.createEmptyBorder(12,20,12,20)
        ));

        playerLabel = new JLabel("♚ Black");
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        playerLabel.setForeground(new Color(248,242,233));
        scoreLabel.setForeground(new Color(248,242,233));

        add(playerLabel, BorderLayout.WEST);
        add(scoreLabel, BorderLayout.EAST);

        setPreferredSize(new Dimension(0, 65));

        eventBus.subscribe(ScoreChangedEvent.class, this);
    }

    @Override
    public void onEvent(ScoreChangedEvent event) {
        // ניקוד השחור מעודכן לפי הניקוד שהלבן צבר (או להיפך, תกฎ המערכת שלך - כאן מעודכן לפי ה-blackScore)
        setScore(event.getBlackScore());
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setPlayerName(String name) {
        playerLabel.setText("♚ " + name);
    }
}