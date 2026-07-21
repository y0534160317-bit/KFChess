package org.kfchess.input;

import org.kfchess.model.BoardGeometry;
import org.kfchess.model.Position;
import org.kfchess.model.CoordinateMapper;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class InteractionHandler implements MouseListener {

    private static final long CLICK_THRESHOLD = 200;

    private final GameClient actions;
    private Position selectedPosition;
    private long lastClickTime;

    // GameWindow מזריק ספק של גיאומטריית הלוח.
    // כך אין תלות ב-BoardPanel ואין צורך לעדכן את ה-Controller בכל ציור.
    private BoardGeometry boardGeometry;

    public InteractionHandler(GameClient actions) {
        this.actions = actions;
    }

    public void setBoardGeometry(BoardGeometry boardGeometry) {
        this.boardGeometry = boardGeometry;
    }



    @Override
    public void mousePressed(MouseEvent e) {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < CLICK_THRESHOLD) {
            return;
        }
        lastClickTime = currentTime;

        if (boardGeometry == null) {
            return;
        }

        Position pos = CoordinateMapper.toPosition(
                e.getX(),
                e.getY(),
                boardGeometry        );

        if (pos == null) {
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            handleJump(pos);
            return;
        }

        handleClick(pos);
    }

    public void handleClick(Position clickedPos) {

        if (clickedPos == null || actions.isGameOver()) {
            return;
        }

        if (!actions.isPositionWithinBounds(clickedPos)) {
            clearSelection();
            return;
        }

        if (selectedPosition == null) {
            trySelect(clickedPos);
            return;
        }

        if (actions.arePiecesSameColor(selectedPosition, clickedPos)) {
            trySelect(clickedPos);
            return;
        }

        Position source = selectedPosition;
        clearSelection();

        if (source.equals(clickedPos)) {
            return;
        }

        actions.requestMove(source, clickedPos);
    }

    public void handleJump(Position pos) {

        if (actions.isGameOver()) {
            return;
        }

        if (!actions.isPositionWithinBounds(pos)) {
            return;
        }

        clearSelection();
        actions.requestJump(pos);
    }

    private void trySelect(Position pos) {
        if (actions.hasSelectablePieceAt(pos)) {
            selectedPosition = pos;
        }
    }

    public void clearSelection() {
        selectedPosition = null;
    }

    public Position getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
}
