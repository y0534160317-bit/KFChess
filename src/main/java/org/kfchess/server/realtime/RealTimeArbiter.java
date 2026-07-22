package org.kfchess.server.realtime;

import org.kfchess.client.view.PieceVisualState;
import org.kfchess.server.model.Board;
import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;
import org.kfchess.client.view.AnimationState;
import org.kfchess.client.view.PieceAnimationState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeArbiter {
    public static final long MOVE_DURATION_PER_SQUARE = 1000;

    public static final long JUMP_DURATION = 1000;

    private final Board board;
    private final CollisionResolver collisionResolver;
    private final List<ActiveMotion> activeMotions;
    private final MotionConflictResolver conflictResolver =
            new MotionConflictResolver();
    private long currentTimeMillis;
    private long nextSequence;
    private boolean kingCaptured;
    private final Map<Piece, PieceAnimationState> animationStates = new HashMap<>();
    private Piece.Color winner;

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

        getAnimationState(piece).setState(
                AnimationState.MOVE,
                currentTimeMillis
        );

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

        getAnimationState(piece).setState(
                AnimationState.JUMP,
                currentTimeMillis
        );

        activeMotions.add(new ActiveMotion(
                ++nextSequence, piece, position, position,
                currentTimeMillis, currentTimeMillis + JUMP_DURATION,
                ActiveMotion.ActionType.JUMP
        ));
    }

    public List<CompletedMove> advanceTime(long milliseconds) {
        if (milliseconds <= 0) {
            return new ArrayList<>();
        }

        List<CompletedMove> completedMoves = new ArrayList<>();
        this.currentTimeMillis += milliseconds;

        List<JumpCapture> jumpCaptures =
                conflictResolver.resolve(
                        activeMotions,
                        board,
                        currentTimeMillis);

        for (JumpCapture capture : jumpCaptures) {

            completedMoves.add(
                    new CompletedMove(
                            capture.getJumper(),
                            capture.getSource(),
                            capture.getDestination(),
                            capture.getCaptured()
                    )
            );
        }

        List<ActiveMotion> completedMotions = new ArrayList<>();

        for (ActiveMotion motion : activeMotions) {
            if (!motion.isCancelled() && motion.isComplete(currentTimeMillis)) {
                completedMotions.add(motion);
            }
        }

        for (ActiveMotion motion : completedMotions) {

            if (motion.isJump()) {
                getAnimationState(motion.getPiece())
                        .setState(AnimationState.SHORT_REST,
                                currentTimeMillis);

                System.out.println(
                        motion.getPiece() + " -> SHORT_REST"
                );
                continue;
            }

            Piece movingPiece = motion.getPiece();
            Position src = motion.getSource();
            Position dest = motion.getDestination();

            Piece currentAtSource = board.getPiece(src);

            Piece target = board.getPiece(dest);

            System.out.println("Captured = " + target);
            if (target != null && target.getColor() != movingPiece.getColor()) {
                if (target.getType() == Piece.Type.KING) {
                    kingCaptured = true;
                    winner = movingPiece.getColor();

                }
            }
            // תנועה לוגית נקייה במקום שתי קריאות setPiece גולמיות
            if (currentAtSource != null && currentAtSource.equals(movingPiece)) {
                board.executeMove(src, dest);
                completedMoves.add(
                        new CompletedMove(
                                movingPiece,
                                src,
                                dest,
                                target
                        )
                );
            }

            // הכתרה מבוקרת באמצעות promotePawn
            if (movingPiece.getType() == Piece.Type.PAWN) {
                int targetRow = dest.getRow();
                if (targetRow == 0 || targetRow == board.getHeight() - 1) {
                    board.promotePawn(new Position(targetRow, dest.getCol()), movingPiece.getColor());
                }
            }

            getAnimationState(movingPiece)
                    .setState(AnimationState.LONG_REST, currentTimeMillis);
        }

        for (PieceAnimationState state : animationStates.values()) {

            if (state.getState() == AnimationState.SHORT_REST &&
                    currentTimeMillis - state.getStateStartTime() >= 500) {

                state.setState(AnimationState.IDLE, currentTimeMillis);
            }

            else if (state.getState() == AnimationState.LONG_REST &&
                    currentTimeMillis - state.getStateStartTime() >= 1000) {

                state.setState(AnimationState.IDLE, currentTimeMillis);
            }
        }

        activeMotions.removeIf(motion -> motion.isCancelled() || motion.isComplete(currentTimeMillis));
        return completedMoves;

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

    private PieceAnimationState getAnimationState(Piece piece) {

        return animationStates.computeIfAbsent(
                piece,
                p -> new PieceAnimationState()
        );
    }

    public AnimationState getAnimationStateOf(Piece piece) {
        return getAnimationState(piece).getState();
    }

    public long getAnimationStateStartTime(Piece piece) {
        return getAnimationState(piece).getStateStartTime();
    }

    public PieceVisualState getVisualState(Piece piece) {

        PieceAnimationState state = getAnimationState(piece);

        return new PieceVisualState(
                state.getState(),
                state.getStateStartTime()
        );
    }


    public Piece.Color getWinner() {
        return winner;
    }

    public boolean isPieceResting(Piece piece) {
        PieceAnimationState state = animationStates.get(piece);

        if (state == null) {
            return false;
        }

        return state.getState() == AnimationState.SHORT_REST
                || state.getState() == AnimationState.LONG_REST;
    }
}