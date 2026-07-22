package org.kfchess.server.network;

import org.kfchess.server.network.processor.CommandProcessor;
import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ClientMessageType;
import org.kfchess.shared.network.ServerMessage;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {

    private final Map<ClientMessageType, CommandProcessor> processors =
            new HashMap<>();

    public void register(
            ClientMessageType type,
            CommandProcessor processor) {

        processors.put(type, processor);
    }

    public ServerMessage dispatch(ClientMessage message) {

        CommandProcessor processor =
                processors.get(message.getType());

        if (processor == null) {
            throw new IllegalArgumentException(
                    "No processor for " + message.getType()
            );
        }

        return processor.process(message);
    }
}