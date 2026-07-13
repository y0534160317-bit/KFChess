package org.example.realtime;

import org.example.model.Piece;
import org.example.model.Position;

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
    public boolean isCancelled() { return cancelled; }

    public void cancel() {
        this.cancelled = true;
    }
}