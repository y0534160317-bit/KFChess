package org.kfchess.io;

import org.kfchess.model.Board;
import org.kfchess.model.BoardView;
import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public class BoardPrinter {

    public void print(BoardView board) {
        int height = board.getHeight();
        int width = board.getWidth();

        for (int r = 0; r < height; r++) {
            StringBuilder rowStr = new StringBuilder();
            for (int c = 0; c < width; c++) {
                Position currentPos = new Position(r, c);
                Piece piece = board.getPiece(currentPos);

                if (piece == null) {
                    rowStr.append(".");
                } else {
                    rowStr.append(piece.toString());
                }

                if (c < width - 1) {
                    rowStr.append(" ");
                }
            }
            System.out.println(rowStr);
        }
    }
}