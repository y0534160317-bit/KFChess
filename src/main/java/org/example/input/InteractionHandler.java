package org.example.input;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.model.CoordinateMapper;

public class InteractionHandler {
    private final Board board;
    private final GameEngineActions actions;
    private Position selectedPosition;

    public InteractionHandler(Board board, GameEngineActions actions) {
        this.board = board;
        this.actions = actions;
        this.selectedPosition = null;
    }

    public void handleClick(int x, int y) {
        if (actions.isGameOver()) return;

        Position clickedPos = CoordinateMapper.toPosition(x, y);

        if (!board.isWithinBounds(clickedPos)) {
            clearSelection();
            return;
        }

        Piece clickedPiece = board.getPiece(clickedPos);

        if (selectedPosition == null) {
            trySelect(clickedPos);
            return;
        }

        // תיקון: החלפת בחירה אם לוחצים על כלי אחר מאותו צבע
        Piece selectedPiece = board.getPiece(selectedPosition);
        if (clickedPiece != null && selectedPiece != null && clickedPiece.getColor() == selectedPiece.getColor()) {
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

        if (!board.isWithinBounds(pos)) {
            return;
        }

        actions.requestJump(pos);
    }

    private void trySelect(Position pos) {
        Piece piece = board.getPiece(pos);
        if (piece == null) {
            return;
        }

        if (actions.isPieceMoving(piece) || !actions.isPieceReady(piece)) {
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