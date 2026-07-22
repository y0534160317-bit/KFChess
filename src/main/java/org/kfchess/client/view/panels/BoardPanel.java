package org.kfchess.client.view.panels;

import org.kfchess.shared.model.BoardGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {

    private BufferedImage image;

    private int boardX;
    private int boardY;
    private int boardSize;

    public BoardPanel() {

        setBackground(new Color(248,242,233));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(160,120,88),3),
                BorderFactory.createEmptyBorder(20,20,20,20)
        ));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            return;
        }

        updateBoardGeometry();

        g.drawImage(
                image,
                boardX,
                boardY,
                boardSize,
                boardSize,
                null
        );
    }


public BoardGeometry getBoardGeometry() {

    updateBoardGeometry();

    return new BoardGeometry(
            boardX,
            boardY,
            boardSize
    );
}

    private void updateBoardGeometry() {
        boardSize = Math.min(getWidth(), getHeight());

        boardX = (getWidth() - boardSize) / 2;
        boardY = (getHeight() - boardSize) / 2;
    }
}