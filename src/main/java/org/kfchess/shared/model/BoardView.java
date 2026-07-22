package org.kfchess.shared.model;


public interface BoardView {

    Piece getPiece(Position position);

    int getWidth();

    int getHeight();

    boolean isWithinBounds(Position position);

    Position findPiece(Piece piece);
}