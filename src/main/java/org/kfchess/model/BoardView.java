package org.kfchess.model;


public interface BoardView {

    Piece getPiece(Position position);

    int getWidth();

    int getHeight();

    boolean isWithinBounds(Position position);

    Position findPiece(Piece piece);
}