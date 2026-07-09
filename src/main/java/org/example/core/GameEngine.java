package org.example.core;

import org.example.input.InteractionHandler;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.io.BoardPrinter;
import org.example.realtime.ActiveMove;
import org.example.realtime.MovementEngine;
import org.example.rules.MoveRules;

public class GameEngine {
    private final Board board;
    private final MovementEngine movementEngine;
    private final MoveRules moveRules;
    private final InteractionHandler interactionHandler;

    public GameEngine(Board board) {
        this.board = board;
        this.movementEngine = new MovementEngine(board);
        this.moveRules = new MoveRules(board);
        this.interactionHandler = new InteractionHandler(board, this);
    }

    public MovementEngine getMovementEngine() { return movementEngine; }
    public MoveRules getMoveRules() { return moveRules; }


    public void handleClick(int x, int y) {
        interactionHandler.handleClick(x, y);
    }

    public void handleJump(int x, int y) {
        interactionHandler.handleJump(x, y);
    }

    public void tryExecuteClickMove(Position from, Position to, Piece selectedPiece) {
        Piece.Color opponentColor = (selectedPiece.getColor() == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;

        if (!movementEngine.isColorMoving(opponentColor) &&
                !movementEngine.isPieceMovingTo(to) &&
                moveRules.isValidMove(from, to, selectedPiece, pos -> movementEngine.isSquareOccupiedByActiveMove(pos, selectedPiece.getColor()))) {

            int distance = moveRules.calculateDistance(from, to);
            long totalTravelTime = distance * MovementEngine.MOVE_DURATION_PER_SQUARE;
            long arrivalTime = movementEngine.getGameTimeMillis() + totalTravelTime;

            movementEngine.addMove(new ActiveMove(from, to, selectedPiece, arrivalTime, false));
        }
    }

    public void tryExecuteJump(Position pos) {
        movementEngine.handleJumpExecution(pos, moveRules);
    }


    public void advanceTime(long millis) {
        movementEngine.advanceTime(millis);
    }

    public void printBoard() {
        BoardPrinter printer = new BoardPrinter();
        printer.print(board);
    }
}