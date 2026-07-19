package org.kfchess.model;

public class ScoreManager {

    private int whiteScore;
    private int blackScore;

    public void addCapturedPiece(Piece captured) {

        int value = getValue(captured);

        if (captured.getColor() == Piece.Color.WHITE) {
            blackScore += value;
        } else {
            whiteScore += value;
        }
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    private int getValue(Piece piece) {

        switch (piece.getType()) {

            case PAWN:
                return 1;

            case KNIGHT:
            case BISHOP:
                return 3;

            case ROOK:
                return 5;

            case QUEEN:
                return 9;

            case KING:
                return 0;
        }

        return 0;
    }
}
