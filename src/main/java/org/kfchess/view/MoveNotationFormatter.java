package org.kfchess.view;

import org.kfchess.events.MoveEvent;
import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public final class MoveNotationFormatter {

    private MoveNotationFormatter() {
    }

    public static String format(MoveEvent event) {

        Piece piece = event.getPiece();
        Position destination = event.getDestination();

        char file = (char) ('a' + destination.getCol());
        int rank = 8 - destination.getRow();

        switch (piece.getType()) {

            case PAWN:
                return "" + file + rank;

            case KNIGHT:
                return "N" + file + rank;

            case BISHOP:
                return "B" + file + rank;

            case ROOK:
                return "R" + file + rank;

            case QUEEN:
                return "Q" + file + rank;

            case KING:
                return "K" + file + rank;

            default:
                return file + "" + rank;
        }
    }
}