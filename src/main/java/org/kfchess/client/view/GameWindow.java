package org.kfchess.client.view;

import org.kfchess.client.input.GameClient;
import org.kfchess.shared.events.EventBus;
import org.kfchess.shared.events.MoveEvent;
import org.kfchess.client.input.InteractionHandler;
import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;
import org.kfchess.client.view.panels.FooterPanel;
import org.kfchess.client.view.panels.HeaderPanel;
import org.kfchess.client.view.panels.MovesPanel;
import org.kfchess.client.view.panels.BoardPanel;


import javax.swing.*;
import java.awt.*;

public final class GameWindow {
    private static final int FRAME_DELAY_MS = 16;
    private final GameClient gameClient;
    private final InteractionHandler controller;
    private final ImgRenderer renderer;
    private JFrame frame;
    private Timer timer;
    private BoardPanel boardPanel;
    private HeaderPanel headerPanel;
    private FooterPanel footerPanel;
    private final EventBus eventBus;

    public GameWindow(GameClient gameClient,
                      InteractionHandler controller,
                      ImgRenderer renderer,
                      HeaderPanel headerPanel,
                      FooterPanel footerPanel,
                      EventBus eventBus){
        this.gameClient = gameClient;
        this.controller = controller;
        this.renderer = renderer;
        this.headerPanel = headerPanel;
        this.footerPanel = footerPanel;
        this.eventBus = eventBus;
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

        frame.getContentPane().setBackground(new Color(248,242,233));

        headerPanel = new HeaderPanel(eventBus);
        footerPanel = new FooterPanel(eventBus);

        MovesPanel leftMovesPanel = new MovesPanel(Piece.Color.BLACK);
        MovesPanel rightMovesPanel = new MovesPanel(Piece.Color.WHITE);

        eventBus.subscribe(
                MoveEvent.class,
                leftMovesPanel
        );

        eventBus.subscribe(
                MoveEvent.class,
                rightMovesPanel
        );

        boardPanel = new BoardPanel();

        boardPanel.addMouseListener(controller);

        JPanel centerPanel = new JPanel(new BorderLayout(15,15));
        centerPanel.setBackground(new Color(248,242,233));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        centerPanel.add(leftMovesPanel, BorderLayout.WEST);
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(rightMovesPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(1300,900));
        frame.setSize(1400,900);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startGameLoop() {
        timer = new Timer(FRAME_DELAY_MS, e -> {
            // 1. קודם כל - נקדם את הזמן של המשחק ב-16 מילי-שניות!
            // (וודא שיש לך מתודה כזו ב-GameEngine שקוראת ל-arbiter.advanceTime)
            gameClient.advanceTime(FRAME_DELAY_MS);

            // 2. עכשיו ניקח תמונת מצב מעודכנת
            Position selected = controller.getSelectedPosition();

            GameSnapshot snapshot = gameClient.snapshot(selected);

            System.out.println(
                    "Snapshot: "
                            + snapshot.getWhiteScore()
                            + " "
                            + snapshot.getBlackScore()
            );


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