package org.kfchess.core;

import org.kfchess.input.GameEngineActions;
import org.kfchess.model.*;
import org.kfchess.realtime.RealTimeArbiter;
import org.kfchess.rules.RuleEngine; // מנוע החוקים הפשוטים שנבנה מייד
import org.kfchess.view.GameSnapshot;
import org.kfchess.view.PieceVisualState;
import org.kfchess.events.MoveEvent;
import org.kfchess.events.MoveObserver;
import org.kfchess.realtime.CompletedMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine implements GameEngineActions {
    private final BoardView board;
    private final RealTimeArbiter arbiter;
    private final RuleEngine ruleEngine;
    private final GameState gameState;
    private final List<MoveObserver> moveObservers = new ArrayList<>();
    private final ScoreManager scoreManager;

    public GameEngine(BoardView board, RealTimeArbiter arbiter, RuleEngine ruleEngine, GameState gameState,ScoreManager scoreManager) {
        this.board = board;
        this.arbiter = arbiter;
        this.ruleEngine = ruleEngine;
        this.gameState = gameState;
        this.scoreManager = scoreManager;
    }

    public void addMoveObserver(MoveObserver observer) {
        moveObservers.add(observer);
    }

    public void removeMoveObserver(MoveObserver observer) {
        moveObservers.remove(observer);
    }

    private void notifyMoveObservers(CompletedMove move) {

        MoveEvent event =
                new MoveEvent(
                        move.getPiece(),
                        move.getSource(),
                        move.getDestination()
                );

        for (MoveObserver observer : moveObservers) {
            observer.onMoveCompleted(event);
        }
    }


    public BoardView getBoard() {
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
        return !arbiter.isPieceInMotion(piece)
                && !arbiter.isPieceResting(piece);
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
       // System.out.println("DEBUG: requestMove received. Source: " + (source == null ? "NULL" : source.getRow() + "," + source.getCol()));
      //  System.out.println("DEBUG: Board reference: " + this.board);
        if (this.board == null) {
            System.err.println("ERROR: Board is null in GameEngine!");
            return;
        }
        Piece piece = board.getPiece(source);
        if (piece == null) {
       //     System.out.println("DEBUG: No piece found at source: " + source.getRow() + "," + source.getCol());
            return;
        }

        if (gameState.isGameOver() || arbiter.isKingCaptured()) {
            return;
        }


        if (piece == null) return;

        LogicalBoardView logicalBoard =
                new LogicalBoardView(
                        board,
                        arbiter.getActiveMotions());

        if (!ruleEngine.isValidMove(logicalBoard, source, destination)) {
        // וידוא חוקיות המהלך במנוע החוקים הטהור לפני תחילת התנועה בזמן אמת
            return; // מהלך לא חוקי (למשל: כלי חבר, או מהלך לא תואם לסוג הכלי)
        }
        if (logicalBoard.isReserved(destination, piece.getColor())) {
            return;
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

        List<CompletedMove> completedMoves =
                arbiter.advanceTime(milliseconds);



        for (CompletedMove move : completedMoves) {
            if (move.getCapturedPiece() != null) {
                scoreManager.addCapturedPiece(move.getCapturedPiece());
            }
            System.out.println(
                    "White=" + scoreManager.getWhiteScore()
                            + " Black=" + scoreManager.getBlackScore()
            );
            notifyMoveObservers(move);
        }
    }

    @Override
    public GameSnapshot snapshot(Position selectedPosition) {
        /*  System.out.println("DEBUG: Snapshot taking board with piece at 2,0: " + (board.getPiece(new Position(2,0)) != null));*/
        Map<Piece, PieceVisualState> visualStates = new HashMap<>();

        for (int r = 0; r < board.getHeight(); r++) {

            for (int c = 0; c < board.getWidth(); c++) {

                Piece piece = board.getPiece(new Position(r,c));

                if(piece != null){

                    visualStates.put(
                            piece,
                            arbiter.getVisualState(piece)
                    );

                }

            }

        }
        return new GameSnapshot(
                this.board,
                this.arbiter.getActiveMotions(),
                selectedPosition,
                this.arbiter.getCurrentTimeMillis(),
                visualStates,
                scoreManager.getWhiteScore(),
                scoreManager.getBlackScore()
        );
    }


}