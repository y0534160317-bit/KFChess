package org.example.realtime;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;

public class RealTimeArbiter {
    public static final long MOVE_DURATION_PER_SQUARE = 1000;

    public static final long JUMP_DURATION = 1000;

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

    public void setGameOver(boolean isGameOver) {
        if (isGameOver) {
            this.kingCaptured = true; // נסמן את הקינג ככבוש כדי ש-isKingCaptured יחזיר true
        }
    }

    public void startMove(Piece piece, Position source, Position destination) {
        if (this.kingCaptured) {
            return; // מונע לחלוטין רישום תנועות חדשות אחרי שהמלך נלכד
        }

        int distance = Math.max(
                Math.abs(destination.getRow() - source.getRow()),
                Math.abs(destination.getCol() - source.getCol())
        );
        long duration = distance * MOVE_DURATION_PER_SQUARE;

        activeMotions.add(new ActiveMotion(
                ++nextSequence, piece, source, destination,
                currentTimeMillis, currentTimeMillis + duration,
                ActiveMotion.ActionType.MOVE
        ));
    }

    public void startJump(Piece piece, Position position) {
        if (this.kingCaptured) {
            return; // מונע לחלוטין רישום תנועות חדשות אחרי שהמלך נלכד
        }
        activeMotions.add(new ActiveMotion(
                ++nextSequence, piece, position, position,
                currentTimeMillis, currentTimeMillis + JUMP_DURATION,
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

            Piece target = board.getPiece(dest);
            if (target != null && target.getColor() != movingPiece.getColor()) {
                if (target.getType() == Piece.Type.KING) {
                    kingCaptured = true;
                }
            }
            // תנועה לוגית נקייה במקום שתי קריאות setPiece גולמיות
            if (currentAtSource != null && currentAtSource.equals(movingPiece)) {
                board.executeMove(src, dest);
            }


            // הכתרה מבוקרת באמצעות promotePawn
            if (movingPiece.getType() == Piece.Type.PAWN) {
                int targetRow = dest.getRow();
                if (targetRow == 0 || targetRow == board.getHeight() - 1) {
                    board.promotePawn(new Position(targetRow, dest.getCol()), movingPiece.getColor());
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

                boolean sameDestination = first.getDestination().equals(second.getDestination());

                boolean headOnCollision = first.getSource().equals(second.getDestination())
                        && first.getDestination().equals(second.getSource());

                if (sameDestination || headOnCollision) {
                    ActiveMotion winner = collisionResolver.determineWinner(first, second);
                    ActiveMotion loser = (winner == first) ? second : first;

                    loser.cancel();

                    // הסרת הכלי המפסיד מהלוח בצורה אנקפסולרית
                    board.removePiece(loser.getSource());
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
                    // תיקון המשתנה: שימוש ב-motion.getSource() במקום loser שלא היה קיים בפונקציה זו
                    board.removePiece(motion.getSource());
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