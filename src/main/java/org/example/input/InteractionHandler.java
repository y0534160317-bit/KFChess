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


        if (!board.isWithinBounds(clickedPos)) return;

        Piece clickedPiece = board.getPiece(clickedPos);

        if (selectedPosition == null) {
            if (clickedPiece != null && !actions.isPieceMovingFrom(clickedPos)){
                selectedPosition = clickedPos;
            }
            return;
        }

        Piece selectedPiece = board.getPiece(selectedPosition);

        if (clickedPiece != null && clickedPiece.getColor() == selectedPiece.getColor()) {
            if (!actions.isPieceMovingFrom(clickedPos)) {
                selectedPosition = clickedPos;
            }
        } else {

            actions.tryExecuteClickMove(selectedPosition, clickedPos, selectedPiece);
            selectedPosition = null;
        }
    }

    public void handleJump(int x, int y) {
        if (actions.isGameOver()) return;

        Position pos = CoordinateMapper.toPosition(x, y);

        if (!board.isWithinBounds(pos)) {
            selectedPosition = null;
            return;
        }

        actions.tryExecuteJump(pos);
        selectedPosition = null;

    }
}
