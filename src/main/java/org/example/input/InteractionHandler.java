package org.example.input;

import org.example.core.GameEngine;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.realtime.ActiveMove;
import org.example.realtime.MovementEngine;
import org.example.rules.MoveRules;


public class InteractionHandler {
    private final Board board;
    private final GameEngine gameEngine;
    private Position selectedPosition;

    public InteractionHandler(Board board, GameEngine gameEngine) {
        this.board = board;
        this.gameEngine = gameEngine;
        this.selectedPosition = null;
    }

    public void handleClick(int x, int y) {
        if (gameEngine.getMovementEngine().isGameOver()) return;

        int row = y / Board.CELL_SIZE;
        int col = x / Board.CELL_SIZE;
        Position clickedPos = new Position(row, col);

        if (!board.isWithinBounds(clickedPos)) return;

        Piece clickedPiece = board.getPiece(clickedPos);

        if (selectedPosition == null) {
            if (clickedPiece != null && !gameEngine.getMovementEngine().isPieceMovingFrom(clickedPos)){
                selectedPosition = clickedPos;
            }
            return;
        }

        Piece selectedPiece = board.getPiece(selectedPosition);

        if (clickedPiece != null && clickedPiece.getColor() == selectedPiece.getColor()) {
            if (!gameEngine.getMovementEngine().isPieceMovingFrom(clickedPos)) {
                selectedPosition = clickedPos;
            }
        } else {

            gameEngine.tryExecuteClickMove(selectedPosition, clickedPos, selectedPiece);
            selectedPosition = null;
        }
    }

    public void handleJump(int x, int y) {
        if (gameEngine.getMovementEngine().isGameOver()) return;

        int row = y / Board.CELL_SIZE;
        int col = x / Board.CELL_SIZE;
        Position pos = new Position(row, col);

        if (!board.isWithinBounds(pos)) {
            selectedPosition = null;
            return;
        }

        gameEngine.tryExecuteJump(pos);
        selectedPosition = null;

    }
}
