package org.example.realtime;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.rules.MoveRules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovementEngine {
    private final Board board;
    private final List<ActiveMove> activeMoves;
    private long gameTimeMillis;
    private boolean isGameOver;

    public static final long MOVE_DURATION_PER_SQUARE = 1000;
    public static final long JUMP_DURATION = 1000;

    public MovementEngine(Board board) {
        this.board = board;
        this.activeMoves = new ArrayList<>();
        this.gameTimeMillis = 0;
        this.isGameOver = false;
    }

    public List<ActiveMove> getActiveMoves() {
        return activeMoves;
    }

    public long getGameTimeMillis() {
        return gameTimeMillis;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public void addMove(ActiveMove move) {
        activeMoves.add(move);
        triggerAirCaptures();
    }

    public void removeMove(ActiveMove move) {
        activeMoves.remove(move);
    }

    public boolean isPieceMovingFrom(Position pos) {
        for (ActiveMove move : activeMoves) {
            if (move.getFrom().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPieceMovingTo(Position pos) {
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos) && !move.isJump()) {
                return true;
            }
        }
        return false;
    }

    public boolean isColorMoving(Piece.Color color) {
        for (ActiveMove move : activeMoves) {
            if (move.getPiece().getColor() == color && !move.isJump()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSquareOccupiedByActiveMove(Position pos, Piece.Color movingColor) {
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos) && move.getPiece().getColor() == movingColor && !move.isJump()) {
                return true;
            }
        }
        return false;
    }

    public void triggerAirCaptures() {
        ActiveMove activeJump = null;
        for (ActiveMove move : activeMoves) {
            if (move.isJump()) {
                activeJump = move;
                break;
            }
        }

        if (activeJump == null) return;

        Iterator<ActiveMove> iterator = activeMoves.iterator();
        while (iterator.hasNext()) {
            ActiveMove move = iterator.next();
            if (!move.isJump() && move.getTo().equals(activeJump.getTo()) && move.getPiece().getColor() != activeJump.getPiece().getColor()) {
                int deltaRow = Math.abs(move.getTo().getRow() - move.getFrom().getRow());
                int deltaCol = Math.abs(move.getTo().getCol() - move.getFrom().getCol());
                int enemyDistance = Math.max(deltaRow, deltaCol);

                // שימוש בלעדי במתודות החדשות (הסרנו את הכפילות הישנה)
                long enemyStartTime = move.getStartTimeMillis(enemyDistance);
                long jumpStartTime = activeJump.getStartTimeMillis(0);

                if (jumpStartTime == enemyStartTime) {
                    board.setPiece(move.getFrom().getRow(), move.getFrom().getCol(), null);
                    iterator.remove();
                }
            }
        }
    }

    public void applyMoveToBoard(Position from, Position to) {
        Piece targetPiece = board.getPiece(to);
        if (targetPiece != null && targetPiece.getType() == Piece.Type.KING) {
            isGameOver = true;
        }
        board.movePiece(from, to);
    }

    public void handleJumpExecution(Position pos, MoveRules moveRules) {
        Piece piece = board.getPiece(pos);
        if (piece == null || isPieceMovingFrom(pos)) return;

        ActiveMove threateningEnemyMove = null;
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos) && move.getPiece().getColor() != piece.getColor()) {
                int distance = moveRules.calculateDistance(move.getFrom(), move.getTo());
                long moveStartTime = move.getStartTimeMillis(distance);
                if (gameTimeMillis > moveStartTime) {
                    threateningEnemyMove = move;
                    break;
                }
            }
        }

        if (threateningEnemyMove != null) {
            applyMoveToBoard(threateningEnemyMove.getFrom(), threateningEnemyMove.getTo());
            handlePawnPromotion(threateningEnemyMove);
            removeMove(threateningEnemyMove);
        } else {
            long arrivalTime = gameTimeMillis + JUMP_DURATION;
            addMove(new ActiveMove(pos, pos, piece, arrivalTime, true));
        }
    }

    public void advanceTime(long millis) {
        if (millis <= 0) return;

        this.gameTimeMillis += millis;

        List<ActiveMove> completedMoves = new ArrayList<>();
        List<ActiveMove> completedJumps = new ArrayList<>();

        Iterator<ActiveMove> iterator = activeMoves.iterator();
        while (iterator.hasNext()) {
            ActiveMove move = iterator.next();
            if (move.isComplete(this.gameTimeMillis)) {
                if (move.isJump()) {
                    completedJumps.add(move);
                } else {
                    completedMoves.add(move);
                }
                iterator.remove();
            }
        }

        for (ActiveMove normalMove : completedMoves) {
            boolean capturedInAir = false;

            for (ActiveMove jumpMove : completedJumps) {
                if (jumpMove.getTo().equals(normalMove.getTo()) && jumpMove.getPiece().getColor() != normalMove.getPiece().getColor()) {
                    capturedInAir = true;
                    break;
                }
            }

            if (capturedInAir) {
                continue;
            }

            Piece targetPiece = board.getPiece(normalMove.getTo());
            if (targetPiece != null && targetPiece.getType() == Piece.Type.KING) {
                isGameOver = true;
            }

            board.movePiece(normalMove.getFrom(), normalMove.getTo());
            handlePawnPromotion(normalMove);
        }
    }

    public void handlePawnPromotion(ActiveMove move) {
        Piece movedPiece = move.getPiece();
        if (movedPiece.getType() == Piece.Type.PAWN) {
            int targetRow = move.getTo().getRow();
            boolean isWhitePromotion = (movedPiece.getColor() == Piece.Color.WHITE && targetRow == 0);
            boolean isBlackPromotion = (movedPiece.getColor() == Piece.Color.BLACK && targetRow == board.getHeight() - 1);

            if (isWhitePromotion || isBlackPromotion) {
                board.setPiece(targetRow, move.getTo().getCol(), new Piece(movedPiece.getColor(), Piece.Type.QUEEN));
            }
        }
    }
}