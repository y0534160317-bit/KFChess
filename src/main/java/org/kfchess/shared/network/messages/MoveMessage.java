package org.kfchess.shared.network.messages;

import org.kfchess.shared.model.Position;
import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ClientMessageType;

public class MoveMessage extends ClientMessage {

    private Position source;
    private Position destination;

    public MoveMessage() {
    }

    public MoveMessage(Position source, Position destination) {
        this.source = source;
        this.destination = destination;
    }

    public Position getSource() {
        return source;
    }

    public void setSource(Position source) {
        this.source = source;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }
}