package org.example.input;

import org.example.model.Piece;
import org.example.model.Position;
import org.example.view.GameSnapshot;

public interface GameEngineActions {

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
