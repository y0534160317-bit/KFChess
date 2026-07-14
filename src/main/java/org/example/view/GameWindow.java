package org.example.view;

import org.example.core.GameEngine;
import org.example.input.InteractionHandler;
import org.example.model.Position;


import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class GameWindow {
    private static final int FRAME_DELAY_MS = 16;
    private final GameEngine engine;
    private final InteractionHandler controller;
    private final ImgRenderer renderer;
    private JFrame frame;
    private JLabel imageLabel;
    private Timer timer;

    public GameWindow(GameEngine engine, InteractionHandler controller, ImgRenderer renderer) {
        this.engine = engine;
        this.controller = controller;
        this.renderer = renderer;
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            createWindow();
            startGameLoop();
        });
    }

    private void createWindow() {
        frame = new JFrame("KFChess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imageLabel = new JLabel();

        // כאן אנחנו מחברים את ה-controller (שהוא ה-InteractionHandler) ישירות ל-label
        // בהנחה ש-InteractionHandler מממש MouseListener
        imageLabel.addMouseListener(controller);

        frame.add(imageLabel);
        frame.setSize(800, 820); // גודל ראשוני, ניתן להתאים לפי הצורך
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /*private void startGameLoop() {
        timer = new Timer(FRAME_DELAY_MS, e -> {
            Position selected = controller.getSelectedPosition();
            GameSnapshot snapshot = engine.snapshot(selected);

            Img currentFrame = renderer.render(snapshot);
            imageLabel.setIcon(new ImageIcon(currentFrame.get()));
        });
        timer.start();
    }
    */
    private void startGameLoop() {
        timer = new Timer(FRAME_DELAY_MS, e -> {
            // 1. קודם כל - נקדם את הזמן של המשחק ב-16 מילי-שניות!
            // (וודא שיש לך מתודה כזו ב-GameEngine שקוראת ל-arbiter.advanceTime)
            engine.advanceTime(FRAME_DELAY_MS);

            // 2. עכשיו ניקח תמונת מצב מעודכנת
            Position selected = controller.getSelectedPosition();
            GameSnapshot snapshot = engine.snapshot(selected);

            // 3. נרנדר ונציג
            Img currentFrame = renderer.render(snapshot);
            imageLabel.setIcon(new ImageIcon(currentFrame.get()));
        });
        timer.start();
    }

}