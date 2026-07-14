package org.example.core;

import org.example.input.GameEngineActions;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.model.GameState;
import org.example.realtime.RealTimeArbiter;
import org.example.rules.RuleEngine; // מנוע החוקים הפשוטים שנבנה מייד
import org.example.input.InteractionHandler;
import org.example.view.GameSnapshot;

public class GameEngine implements GameEngineActions {
    private final Board board;
    private final RealTimeArbiter arbiter;
    private final RuleEngine ruleEngine;
    private final GameState gameState;


    public GameEngine(Board board, RealTimeArbiter arbiter, RuleEngine ruleEngine, GameState gameState) {
        this.board = board;
        this.arbiter = arbiter;
        this.ruleEngine = ruleEngine;
        this.gameState = gameState;
    }

    public Board getBoard() {
        return this.board;
    }

    @Override
    public boolean isGameOver() {
        if (arbiter.isKingCaptured()) {
            gameState.setGameOver(true);
        }
        return gameState.isGameOver();
    }


    @Override
    public boolean isPieceMoving(Piece piece) {
        return arbiter.isPieceInMotion(piece);
    }

    @Override
    public boolean isPieceReady(Piece piece) {
        // הכלי מוכן אם הוא לא נמצא כרגע בתנועה באוויר
        return !arbiter.isPieceInMotion(piece);
    }

    @Override
    public boolean isPositionWithinBounds(Position pos) {
        return board.isWithinBounds(pos);
    }

    @Override
    public boolean hasSelectablePieceAt(Position pos) {
        // 1. בודקים אם בכלל יש כלי במיקום הזה בלוח
        Piece piece = board.getPiece(pos);
        if (piece == null) {
            return false;
        }

        // 2. בודקים האם הכלי במצב שמאפשר בחירה (לא בתנועה ומוכן)
        return isPieceReady(piece) && !isPieceMoving(piece);
    }

    @Override
    public boolean arePiecesSameColor(Position pos1, Position pos2) {
        Piece piece1 = board.getPiece(pos1);
        Piece piece2 = board.getPiece(pos2);

        // אם אחד המיקומים ריק, הם בטוח לא מאותו הצבע
        if (piece1 == null || piece2 == null) {
            return false;
        }

        // השוואת הצבעים של שני הכלים
        return piece1.getColor() == piece2.getColor();
    }

    @Override
    public void requestMove(Position source, Position destination) {
        System.out.println("DEBUG: requestMove received. Source: " + (source == null ? "NULL" : source.getRow() + "," + source.getCol()));
        System.out.println("DEBUG: Board reference: " + this.board);
        if (this.board == null) {
            System.err.println("ERROR: Board is null in GameEngine!");
            return;
        }
        Piece piece = board.getPiece(source);
        if (piece == null) {
            System.out.println("DEBUG: No piece found at source: " + source.getRow() + "," + source.getCol());
            return;
        }

        if (gameState.isGameOver() || arbiter.isKingCaptured()) {
            return;
        }


        if (piece == null) return;

        // וידוא חוקיות המהלך במנוע החוקים הטהור לפני תחילת התנועה בזמן אמת
        if (!ruleEngine.isValidMove(board, source, destination)) {
            System.out.println("DEBUG: Move rejected by engine!");
            return; // מהלך לא חוקי (למשל: כלי חבר, או מהלך לא תואם לסוג הכלי)
        }

        // שליחת הפקודה לארביטר שיתחיל את התנועה על ציר הזמן
        arbiter.startMove(piece, source, destination);
    }

    @Override
    public void requestJump(Position position) {
        if (gameState.isGameOver()) {
            return;
        }

        Piece piece = board.getPiece(position);
        if (piece == null) return;

        // שליחת פקודת קפיצה לארביטר
        arbiter.startJump(piece, position);
    }

    @Override
    public void advanceTime(long milliseconds) {
        arbiter.advanceTime(milliseconds);
    }

    @Override
    public GameSnapshot snapshot(Position selectedPosition) {
      /*  System.out.println("DEBUG: Snapshot taking board with piece at 2,0: " + (board.getPiece(new Position(2,0)) != null));*/
        return new GameSnapshot(
                this.board,
                this.arbiter.getActiveMotions(),
                selectedPosition, // עובר כפרמטר
                this.arbiter.getCurrentTimeMillis()
        );
    }
}