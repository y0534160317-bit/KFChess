package org.example.core;

import org.example.input.InteractionHandler;
import org.example.model.Board;
import org.example.io.BoardPrinter;
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
        this.interactionHandler = new InteractionHandler(board, this.movementEngine, this.moveRules);
    }

    public void handleClick(int x, int y) {
        interactionHandler.handleClick(x, y);
    }

    public void handleJump(int x, int y) {
        interactionHandler.handleJump(x, y);
    }

    public void advanceTime(long millis) {
        movementEngine.advanceTime(millis);
    }

    public void printBoard() {
        BoardPrinter printer = new BoardPrinter();
        printer.print(board);
    }
}