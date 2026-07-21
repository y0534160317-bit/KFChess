package org.kfchess.input;

import org.kfchess.model.Piece;
import org.kfchess.model.Position;
import org.kfchess.view.GameSnapshot;

public interface GameClient {

    boolean isGameOver();

    boolean isPieceMoving(Piece piece);

    boolean isPieceReady(Piece piece);

    boolean isPositionWithinBounds(Position pos);

    boolean hasSelectablePieceAt(Position pos);

    boolean arePiecesSameColor(Position pos1, Position pos2);

    void requestMove(Position source, Position destination);

    void requestJump(Position position);

    void advanceTime(long milliseconds);

    GameSnapshot snapshot(Position selectedPosition);
}
