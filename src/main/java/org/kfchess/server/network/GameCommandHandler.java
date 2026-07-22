package org.kfchess.server.network;

import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public class GameCommandHandler {

    private final CommandDispatcher dispatcher;

    public GameCommandHandler(
            CommandDispatcher dispatcher) {

        this.dispatcher = dispatcher;
    }

    public ServerMessage handle(ClientMessage message) {

        return dispatcher.dispatch(message);

    }
}