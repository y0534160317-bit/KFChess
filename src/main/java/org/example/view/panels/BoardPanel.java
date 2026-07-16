package org.example.view.panels;

import org.example.model.BoardGeometry;
import org.example.model.CoordinateMapper;
import org.example.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {

    private BufferedImage image;

    private int boardX;
    private int boardY;
    private int boardSize;

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