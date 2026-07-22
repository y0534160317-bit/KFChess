package org.kfchess.client.network;

import org.kfchess.client.input.GameClient;
import org.kfchess.client.view.GameSnapshot;
import org.kfchess.server.core.GameEngine;
import org.kfchess.server.network.GameCommandHandler;
import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;
import org.kfchess.shared.network.messages.GameStateMessage;
import org.kfchess.shared.network.messages.MoveMessage;

public class LocalGameClient implements GameClient {

    private final Transport transport;
    private final GameEngine engine;
    private GameSnapshot lastSnapshot;

    public LocalGameClient(GameEngine engine,
                           Transport transport){
        this.engine = engine;
        this.transport = transport;
    }

    @Override
    public boolean isGameOver() {
        return engine.isGameOver();
    }

    @Override
    public boolean isPieceMoving(Piece piece) {
        return engine.isPieceMoving(piece);
    }

    @Override
    public boolean isPieceReady(Piece piece) {
        return engine.isPieceReady(piece);
    }

    @Override
    public boolean isPositionWithinBounds(Position pos) {
        return engine.isPositionWithinBounds(pos);
    }

    @Override
    public boolean hasSelectablePieceAt(Position pos) {
        return engine.hasSelectablePieceAt(pos);
    }

    @Override
    public boolean arePiecesSameColor(Position pos1, Position pos2) {
        return engine.arePiecesSameColor(pos1, pos2);
    }

    @Override
    public void requestMove(Position source, Position destination) {
        MoveMessage message =
                new   MoveMessage(source, destination);

        GameStateMessage response =
                (GameStateMessage) transport.send(message);

        lastSnapshot = response.getSnapshot();
    }

    @Override
    public void requestJump(Position position) {
        engine.requestJump(position);
    }

    @Override
    public void advanceTime(long milliseconds) {
        engine.advanceTime(milliseconds);
    }

    @Override
    public GameSnapshot snapshot(Position selectedPosition) {
        return engine.snapshot(selectedPosition);
    }
}