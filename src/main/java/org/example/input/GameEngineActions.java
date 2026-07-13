package org.example.input;

import org.example.model.Piece;
import org.example.model.Position;

public interface GameEngineActions {

    boolean isGameOver();

    boolean isPieceMoving(Piece piece);

    boolean isPieceReady(Piece piece);

    void requestMove(Position source, Position destination);

    void requestJump(Position position);

}
