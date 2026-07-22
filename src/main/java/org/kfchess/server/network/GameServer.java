package org.kfchess.server.network;

import org.kfchess.client.view.GameSnapshot;
import org.kfchess.shared.model.Position;

public interface GameServer {

    void requestMove(Position source, Position destination);

    void requestJump(Position source, Position destination);

    void advanceTime(long milliseconds);

    GameSnapshot snapshot(Position selectedPosition);

    boolean isGameOver();
}