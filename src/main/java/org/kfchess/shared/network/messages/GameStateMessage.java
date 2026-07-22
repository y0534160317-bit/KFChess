package org.kfchess.shared.network.messages;

import org.kfchess.client.view.GameSnapshot;
import org.kfchess.shared.network.ServerMessage;
import org.kfchess.shared.network.ServerMessageType;

public class GameStateMessage extends ServerMessage {

    private final GameSnapshot snapshot;

    public GameStateMessage(GameSnapshot snapshot) {
        super(ServerMessageType.GAME_STATE);
        this.snapshot = snapshot;
    }

    public GameSnapshot getSnapshot() {
        return snapshot;
    }
}