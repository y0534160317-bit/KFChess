package org.kfchess.client.network;

import org.kfchess.server.network.GameCommandHandler;
import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public class LocalTransport implements Transport {

    private final GameCommandHandler commandHandler;

    public LocalTransport(GameCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public ServerMessage send(ClientMessage message) {
        return commandHandler.handle(message);
    }
}