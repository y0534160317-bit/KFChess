package org.example.input;

import org.example.model.Position;
import org.example.model.CoordinateMapper;

public class InteractionHandler {
    private final GameEngineActions actions;
    private Position selectedPosition;

    public InteractionHandler(GameEngineActions actions) {
        this.actions = actions;
        this.selectedPosition = null;
    }

    public void handleClick(int x, int y) {
        if (actions.isGameOver()) return;

        Position clickedPos = CoordinateMapper.toPosition(x, y);

        // שימוש במתודת גבולות מופשטת דרך ה-Actions
        if (!actions.isPositionWithinBounds(clickedPos)) {
            clearSelection();
            return;
        }

        if (selectedPosition == null) {
            trySelect(clickedPos);
            return;
        }

        // שאילתת צבע מופשטת ללא חשיפת אובייקט הנתונים הפנימי
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

    public void handleJump(int x, int y) {
        if (actions.isGameOver()) {
            clearSelection();
            return;
        }

        Position pos = CoordinateMapper.toPosition(x, y);
        clearSelection();

        if (!actions.isPositionWithinBounds(pos)) {
            return;
        }

        actions.requestJump(pos);
    }

    private void trySelect(Position pos) {
        if (!actions.hasSelectablePieceAt(pos)) {
            return;
        }
        selectedPosition = pos;
    }

    public void clearSelection() {
        this.selectedPosition = null;
    }

    public Position getSelectedPosition() {
        return selectedPosition;
    }
}