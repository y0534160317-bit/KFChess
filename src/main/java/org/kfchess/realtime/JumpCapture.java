package org.kfchess.realtime;

import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public class JumpCapture {

    private final Piece jumper;
    private final Piece captured;
    private final Position source;
    private final Position destination;

    public JumpCapture(
            Piece jumper,
            Piece captured,
            Position source,
            Position destination) {

        this.jumper = jumper;
        this.captured = captured;
        this.source = source;
        this.destination = destination;
    }


    public Piece getJumper() {
        return jumper;
    }

    public Piece getCaptured() {
        return captured;
    }

    public Position getSource() {
        return source;
    }

    public Position getDestination() {
        return destination;
    }
}