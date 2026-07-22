package org.kfchess.server.realtime;

import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;

public class CompletedMove {

    private final Piece piece;
    private final Position source;
    private final Position destination;
    private final Piece capturedPiece;

    public CompletedMove(Piece piece,
                         Position source,
                         Position destination,
                         Piece capturedPiece) {

        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.capturedPiece = capturedPiece;
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

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}