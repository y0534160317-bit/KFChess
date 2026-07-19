package org.kfchess.realtime;

import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public class ActiveMotion {
    public enum ActionType {
        MOVE, JUMP
    }

    private final long sequence; // מספר סידורי לשבירת שוויון
    private final Piece piece;
    private final Position source;
    private final Position destination;
    private final long startTimeMillis;
    private final long arrivalTimeMillis;
    private final ActionType actionType;
    private boolean cancelled;
    private String currentState;

    public ActiveMotion(long sequence, Piece piece, Position source, Position destination,
                        long startTimeMillis, long arrivalTimeMillis, ActionType actionType) {
        this.sequence = sequence;
        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.startTimeMillis = startTimeMillis;
        this.arrivalTimeMillis = arrivalTimeMillis;
        this.actionType = actionType;
        this.cancelled = false;
        this.currentState = (actionType == ActionType.JUMP) ? "jump" : "move";
    }

    public boolean isComplete(long currentTimeMillis) {
        return currentTimeMillis >= arrivalTimeMillis;
    }

    public boolean isJump() {
        return actionType == ActionType.JUMP;
    }

    public long getSequence() { return sequence; }
    public Piece getPiece() { return piece; }
    public Position getSource() { return source; }
    public Position getDestination() { return destination; }
    public long getStartTimeMillis() { return startTimeMillis; }
    public long getArrivalTimeMillis() { return arrivalTimeMillis; }
    public boolean isCancelled() { return cancelled; }

    public void cancel() {
        this.cancelled = true;
    }

    public void updateState(long currentTimeMillis) {
        // לוגיקה של מעבר בין מצבים (למשל: סיום תנועה עובר למנוחה)
        if (isComplete(currentTimeMillis)) {
            this.currentState = "short_rest";
        }
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getCurrentState(long currentTime) {
        if (isComplete(currentTime)) {
            long timeSinceArrival = currentTime - arrivalTimeMillis;
            // לאחר הגעה ליעד: אם עברו יותר מ-X שניות, עוברים למנוחה ארוכה
            return (timeSinceArrival > 5000) ? "long_rest" : "short_rest";
        }

        // בזמן תנועה:
        return isJump() ? "jump" : "move";
    }



}