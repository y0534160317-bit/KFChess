package org.kfchess.server.network.processor;

import org.kfchess.client.view.GameSnapshot;
import org.kfchess.server.core.GameEngine;
import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;
import org.kfchess.shared.network.messages.GameStateMessage;
import org.kfchess.shared.network.messages.MoveMessage;

public class MoveProcessor implements CommandProcessor {

    private final GameEngine engine;

    public MoveProcessor(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public ServerMessage process(ClientMessage message) {

        MoveMessage move = (MoveMessage) message;

        engine.requestMove(
                move.getSource(),
                move.getDestination()
        );

        GameSnapshot snapshot =
                engine.snapshot(null);

        return new GameStateMessage(snapshot);
    }
}