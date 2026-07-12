package org.example.input;

import org.example.model.Piece;
import org.example.model.Position;

public interface GameEngineActions {

    boolean isGameOver();
    boolean isPieceMovingFrom(Position pos);
    void tryExecuteClickMove(Position from, Position to, Piece selectedPiece);
    void tryExecuteJump(Position pos);

}
