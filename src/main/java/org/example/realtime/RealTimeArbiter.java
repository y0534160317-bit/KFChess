package org.example.realtime;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;

public class RealTimeArbiter {
    public static final long MOVE_MS_PER_CELL = 1000;
    public static final long JUMP_DURATION_MS = 1000;

    private final Board board;
    private final CollisionResolver collisionResolver;
    private final List<ActiveMotion> activeMotions;
    private long currentTimeMillis;
    private long nextSequence;
    private boolean kingCaptured;

    public RealTimeArbiter(Board board, CollisionResolver collisionResolver) {
        this.board = board;
        this.collisionResolver = collisionResolver;
        this.activeMotions = new ArrayList<>();
        this.currentTimeMillis = 0;
        this.nextSequence = 0;
        this.kingCaptured = false;
    }

    public void startMove(Piece piece, Position source, Position destination) {
        int distance = Math.max(
                Math.abs(destination.getRow() - source.getRow()),
                Math.abs(destination.getCol() - source.getCol())
        );
        long duration = distance * MOVE_MS_PER_CELL;

        activeMotions.add(new ActiveMotion(
                ++nextSequence, piece, source, destination,
                currentTimeMillis, currentTimeMillis + duration,
                ActiveMotion.ActionType.MOVE
        ));
    }

    public void startJump(Piece piece, Position position) {
        activeMotions.add(new ActiveMotion(
                ++nextSequence, piece, position, position,
                currentTimeMillis, currentTimeMillis + JUMP_DURATION_MS,
                ActiveMotion.ActionType.JUMP
        ));
    }

    public void advanceTime(long milliseconds) {
        if (milliseconds <= 0) return;
        this.currentTimeMillis += milliseconds;

        resolveAirCollisions();
        resolveAirCapturesByJumps();

        List<ActiveMotion> completedMotions = new ArrayList<>();
        for (ActiveMotion motion : activeMotions) {
            if (!motion.isCancelled() && motion.isComplete(currentTimeMillis)) {
                completedMotions.add(motion);
            }
        }

        for (ActiveMotion motion : completedMotions) {
            if (motion.isJump()) continue;

            Piece movingPiece = motion.getPiece();
            Position src = motion.getSource();
            Position dest = motion.getDestination();

            Piece currentAtSource = board.getPiece(src);
            if (currentAtSource != null && currentAtSource.equals(movingPiece)) {
                board.setPiece(src.getRow(), src.getCol(), null);
            }

            Piece target = board.getPiece(dest);
            if (target != null && target.getColor() != movingPiece.getColor()) {
                if (target.getType() == Piece.Type.KING) {
                    kingCaptured = true;
                }
            }

            board.setPiece(dest.getRow(), dest.getCol(), movingPiece);

            if (movingPiece.getType() == Piece.Type.PAWN) {
                int targetRow = dest.getRow();
                if (targetRow == 0 || targetRow == board.getHeight() - 1) {
                    board.setPiece(targetRow, dest.getCol(),
                            new Piece(movingPiece.getColor(), Piece.Type.QUEEN));
                }
            }
        }

        activeMotions.removeIf(motion -> motion.isCancelled() || motion.isComplete(currentTimeMillis));
    }

    private void resolveAirCollisions() {
        for (int i = 0; i < activeMotions.size(); i++) {
            ActiveMotion first = activeMotions.get(i);
            if (first.isCancelled() || first.isJump()) continue;

            for (int j = i + 1; j < activeMotions.size(); j++) {
                ActiveMotion second = activeMotions.get(j);
                if (second.isCancelled() || second.isJump()) continue;

                if (first.getPiece().getColor() == second.getPiece().getColor()) continue;

                // בדיקה 1: הגעה לאותו יעד משותף
                boolean sameDestination = first.getDestination().equals(second.getDestination());

                // בדיקה 2: התנגשות חזיתית באוויר (האחד נע לתוך המקור של השני ולהפך)
                boolean headOnCollision = first.getSource().equals(second.getDestination())
                        && first.getDestination().equals(second.getSource());

                if (sameDestination || headOnCollision) {
                    ActiveMotion winner = collisionResolver.determineWinner(first, second);
                    ActiveMotion loser = (winner == first) ? second : first;

                    loser.cancel();

                    // מחיקת הכלי המפסיד מהלוח כדי שלא יופיע בסיום
                    board.setPiece(loser.getSource().getRow(), loser.getSource().getCol(), null);
                }
            }
        }
    }

    private void resolveAirCapturesByJumps() {
        ActiveMotion activeJump = null;
        for (ActiveMotion motion : activeMotions) {
            if (motion.isJump() && !motion.isCancelled()) {
                activeJump = motion;
                break;
            }
        }

        if (activeJump == null) return;

        for (ActiveMotion motion : activeMotions) {
            if (!motion.isJump() && !motion.isCancelled()
                    && motion.getDestination().equals(activeJump.getDestination())
                    && motion.getPiece().getColor() != activeJump.getPiece().getColor()) {

                if (motion.getStartTimeMillis() == activeJump.getStartTimeMillis()) {
                    motion.cancel();
                    board.setPiece(motion.getSource().getRow(), motion.getSource().getCol(), null);
                }
            }
        }
    }

    public boolean isPieceInMotion(Piece piece) {
        for (ActiveMotion motion : activeMotions) {
            if (!motion.isCancelled() && motion.getPiece().equals(piece)) {
                return true;
            }
        }
        return false;
    }

    public boolean isKingCaptured() {
        return kingCaptured;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public List<ActiveMotion> getActiveMotions() {
        return activeMotions;
    }
}