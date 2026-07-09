package org.example.io;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

public class BoardPrinter {

    // Prints the board configuration precisely as expected by the VPL platform
    public void print(Board board) {

        int height = board.getHeight();
        int width = board.getWidth();

        for (int r = 0; r < height; r++) {
            StringBuilder rowStr = new StringBuilder();
            for (int c = 0; c < width; c++) {

                Piece piece = board.getPiece(new Position(r, c));

                if (piece == null) {
                    rowStr.append("."); // Prints a dot for empty squares
                } else {
                    rowStr.append(piece.toString()); // Prints the piece token (e.g., "wK")
                }

                // appends a single space between elements, ensuring no trailing space at the end of a line
                if (c < width - 1) {
                    rowStr.append(" ");
                }
            }
            System.out.println(rowStr); // Prints the completed row
        }
    }
}
