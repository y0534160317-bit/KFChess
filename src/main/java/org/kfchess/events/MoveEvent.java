package org.kfchess.events;

import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public class MoveEvent implements Event{

    private final Piece piece;
    private final Position source;
    private final Position destination;
    private final long gameTimeMillis;

    public MoveEvent(
            Piece piece,
            Position source,
            Position destination,
            long gameTimeMillis) {

        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.gameTimeMillis = gameTimeMillis;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getSource() {
        return source;
    }

    public Position getDestination() {
        return destination;
    }

    public long getGameTimeMillis() {
        return gameTimeMillis;
    }
}