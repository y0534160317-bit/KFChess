package org.example.view;

import org.example.core.GameEngine;
import org.example.input.InteractionHandler;
import org.example.model.Position;
import org.example.view.panels.FooterPanel;
import org.example.view.panels.HeaderPanel;
import org.example.view.panels.MovesPanel;
import org.example.view.panels.BoardPanel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class GameWindow {
    private static final int FRAME_DELAY_MS = 16;
    private final GameEngine engine;
    private final InteractionHandler controller;
    private final ImgRenderer renderer;
    private JFrame frame;
    private Timer timer;
    private BoardPanel boardPanel;

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
        frame.setLayout(new BorderLayout());

        // הפאנלים
        HeaderPanel headerPanel = new HeaderPanel();
        FooterPanel footerPanel = new FooterPanel();

        MovesPanel leftMovesPanel = new MovesPanel();
        MovesPanel rightMovesPanel = new MovesPanel();

        boardPanel = new BoardPanel();

        // הלוח עדיין קולט את העכבר
        boardPanel.addMouseListener(controller);

        // האזור המרכזי
        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(leftMovesPanel, BorderLayout.WEST);
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(rightMovesPanel, BorderLayout.EAST);

        // הרכבת החלון
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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
            boardPanel.setImage(currentFrame.get());

            controller.setBoardGeometry(
                    boardPanel.getBoardGeometry()
            );

        });
        timer.start();
    }

}