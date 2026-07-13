package org.example.realtime;

import org.example.model.Piece;
import org.example.model.Position;

public class ActiveMove {
    private final Position from;
    private final Position to;
    private final Piece piece;
    private final long arrivalTimeMillis;
    private final boolean isJump; // השדה הסופי

    // ודא שחמשת הפרמטרים האלה מופיעים בדיוק כך בתוך הסוגריים של הבנאי:
    public ActiveMove(Position from, Position to, Piece piece, long arrivalTimeMillis, boolean isJump) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.arrivalTimeMillis = arrivalTimeMillis;
        this.isJump = isJump;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getPiece() { return piece; }
    public long getArrivalTimeMillis() { return arrivalTimeMillis; }
    public boolean isJump() { return isJump; }

    public boolean isComplete(long currentTimeMillis) {
        return currentTimeMillis >= arrivalTimeMillis;
    }

    public long getStartTimeMillis(int distance) {
        if (isJump) {
            return arrivalTimeMillis - RealTimeArbiter.JUMP_DURATION;
        }
        return arrivalTimeMillis - (distance * RealTimeArbiter.MOVE_DURATION_PER_SQUARE);
    }
}