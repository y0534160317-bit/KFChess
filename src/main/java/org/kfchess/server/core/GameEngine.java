package org.kfchess.server.core;

import org.kfchess.server.model.LogicalBoardView;
import org.kfchess.server.model.ScoreManager;
import org.kfchess.shared.events.EventBus;
import org.kfchess.input.GameClient;
import org.kfchess.server.realtime.RealTimeArbiter;
import org.kfchess.server.rules.MoveValidationResult;
import org.kfchess.server.rules.RuleEngine; // מנוע החוקים הפשוטים שנבנה מייד
import org.kfchess.client.view.GameSnapshot;
import org.kfchess.client.view.PieceVisualState;
import org.kfchess.shared.events.MoveEvent;
import org.kfchess.server.realtime.CompletedMove;
import org.kfchess.shared.model.BoardView;
import org.kfchess.shared.model.GameState;
import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;
import org.kfchess.shared.server.GameServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine implements GameServer {
    private final BoardView board;
    private final RealTimeArbiter arbiter;
    private final RuleEngine ruleEngine;
    private final GameState gameState;
   // private final List<MoveObserver> moveObservers = new ArrayList<>();
    private final ScoreManager scoreManager;
    private final EventBus eventBus;

    public GameEngine(BoardView board,
                      RealTimeArbiter arbiter,
                      RuleEngine ruleEngine,
                      GameState gameState,
                      ScoreManager scoreManager,
                      EventBus eventBus) {

        this.board = board;
        this.arbiter = arbiter;
        this.ruleEngine = ruleEngine;
        this.gameState = gameState;
        this.scoreManager = scoreManager;
        this.eventBus = eventBus;
    }

//    public void addMoveObserver(MoveObserver observer) {
//        moveObservers.add(observer);
//    }
//
//    public void removeMoveObserver(MoveObserver observer) {
//        moveObservers.remove(observer);
//    }

    private void publishMoveEvent(CompletedMove move) {

        eventBus.publish(
                new MoveEvent(
                        move.getPiece(),
                        move.getSource(),
                        move.getDestination(),
                        arbiter.getCurrentTimeMillis()
                )
        );
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


    public boolean isPieceMoving(Piece piece) {
        return arbiter.isPieceInMotion(piece);
    }


    public boolean isPieceReady(Piece piece) {
        return !arbiter.isPieceInMotion(piece)
                && !arbiter.isPieceResting(piece);
    }


    public boolean isPositionWithinBounds(Position pos) {
        return board.isWithinBounds(pos);
    }

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



        MoveValidationResult validationResult =
                ruleEngine.validateMove(
                        logicalBoard,
                        source,
                        destination);

        if (!validationResult.isValid()) {
            // בעתיד אפשר להשתמש ב-validationResult.getReason()
            return;
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
                scoreManager.handleCapturedPiece(move.getCapturedPiece());            }
            System.out.println(
                    "White=" + scoreManager.getWhiteScore()
                            + " Black=" + scoreManager.getBlackScore()
            );
//            notifyMoveObservers(move);
            publishMoveEvent(move);
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
                board,
                arbiter.getActiveMotions(),
                selectedPosition,
                arbiter.getCurrentTimeMillis(),
                visualStates,
                scoreManager.getWhiteScore(),
                scoreManager.getBlackScore(),
                arbiter.isKingCaptured(),
                arbiter.getWinner()
        );
    }


}