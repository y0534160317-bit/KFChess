package org.example.events;

import org.example.model.Piece;
import org.example.model.Position;

public class MoveEvent {

    private final Piece piece;
    private final Position source;
    private final Position destination;

    public MoveEvent(Piece piece, Position source, Position destination) {
        this.piece = piece;
        this.source = source;
        this.destination = destination;
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
}