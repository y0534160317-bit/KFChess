package org.kfchess.client.network;

import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public class WebSocketTransport implements Transport {

    @Override
    public ServerMessage send(ClientMessage message) {
        throw new UnsupportedOperationException(
                "WebSocket transport is not implemented yet."
        );
    }
}