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

    private long lastClickTime = 0;
    private static final long CLICK_THRESHOLD = 200; // מילי-שניות למניעת לחיצה כפולה

    @Override
    public void mousePressed(MouseEvent e) {
        // הגנה: סינון אירועים שקורים מהר מדי אחד אחרי השני
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < CLICK_THRESHOLD) {
            return;
        }
        lastClickTime = currentTime;

        Position pos = CoordinateMapper.toPosition(e.getX(), e.getY(),
                e.getComponent().getWidth(), e.getComponent().getHeight());

        // בדיקה שהמיקום שהתקבל תקין לפני המשך העיבוד
        if (pos != null) {
            handleClick(pos);
        }
    }

    public void handleClick(Position clickedPos) {
        if (clickedPos == null || actions.isGameOver()) return;

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

        // שמירת ה-Source וביצוע פעולה רק אם יש בחירה תקינה
        Position source = selectedPosition;
        System.out.println("DEBUG: Attempting move from " + source.getRow() + "," + source.getCol() +
                " to " + clickedPos.getRow() + "," + clickedPos.getCol());
        // איפוס הבחירה חייב לקרות לפני שליחת הפקודה למנוע
        clearSelection();

        if (source.equals(clickedPos)) {
            return;
        }

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