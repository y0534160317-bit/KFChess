package org.example.input;

import org.example.model.Position;
import org.example.model.CoordinateMapper;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class InteractionHandler implements MouseListener {
    private final GameEngineActions actions;
    private Position selectedPosition;

    public InteractionHandler(GameEngineActions actions) {
        this.actions = actions;
        this.selectedPosition = null;
    }

    // נקודת כניסה מהעכבר (מטפלת בפיקסלים)
    @Override
    public void mousePressed(MouseEvent e) {
        Position pos = CoordinateMapper.toPosition(e.getX(), e.getY(),
                e.getComponent().getWidth(),
                e.getComponent().getHeight());
        handleClick(pos);
    }

    // לוגיקת ה-Click הטהורה (מקבלת מיקום לוח)
    public void handleClick(Position clickedPos) {
        if (actions.isGameOver()) return;

        System.out.println("DEBUG: Executing click at -> Row: " + clickedPos.getRow() + ", Col: " + clickedPos.getCol());

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
        actions.requestMove(source, clickedPos);
    }

    // לוגיקת הקפיצה
    public void handleJump(Position pos) {
        if (actions.isGameOver()) return;
        if (!actions.isPositionWithinBounds(pos)) return;

        clearSelection();
        actions.requestJump(pos);
    }

    private void trySelect(Position pos) {
        if (actions.hasSelectablePieceAt(pos)) {
            selectedPosition = pos;
        }
    }

    public void clearSelection() { this.selectedPosition = null; }
    public Position getSelectedPosition() { return selectedPosition; }

    // מתודות ריקות לממשק
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}