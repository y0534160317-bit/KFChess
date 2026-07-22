package org.kfchess.server.network.processor;

import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public interface CommandProcessor {

    ServerMessage process(ClientMessage message);

}