package org.example.core;

import org.example.input.GameEngineActions;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.realtime.RealTimeArbiter;
import org.example.rules.RuleEngine; // מנוע החוקים הפשוטים שנבנה מייד
import org.example.input.InteractionHandler;

public class GameEngine implements GameEngineActions {
    private final Board board;
    private final RealTimeArbiter arbiter;
    private final RuleEngine ruleEngine;

    public GameEngine(Board board, RealTimeArbiter arbiter, RuleEngine ruleEngine) {
        this.board = board;
        this.arbiter = arbiter;
        this.ruleEngine = ruleEngine;
    }

    public Board getBoard() {
        return this.board;
    }

    @Override
    public boolean isGameOver() {
        return arbiter.isKingCaptured();
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
    public void requestMove(Position source, Position destination) {
        if (isGameOver()) return;

        Piece piece = board.getPiece(source);
        if (piece == null) return;

        // וידוא חוקיות המהלך במנוע החוקים הטהור לפני תחילת התנועה בזמן אמת
        if (!ruleEngine.isValidMove(board, source, destination)) {
            return; // מהלך לא חוקי (למשל: כלי חבר, או מהלך לא תואם לסוג הכלי)
        }

        // שליחת הפקודה לארביטר שיתחיל את התנועה על ציר הזמן
        arbiter.startMove(piece, source, destination);
    }

    @Override
    public void requestJump(Position position) {
        if (isGameOver()) return;

        Piece piece = board.getPiece(position);
        if (piece == null) return;

        // שליחת פקודת קפיצה לארביטר
        arbiter.startJump(piece, position);
    }
}