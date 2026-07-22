package org.kfchess.server.rules;

import org.kfchess.shared.model.Piece;

public class PieceRules {

    // Validates if the movement geometry is valid for this specific piece type
    public boolean isValidMoveShape(Piece.Type type, int deltaRow, int deltaCol) {

        int absRow = Math.abs(deltaRow);
        int absCol = Math.abs(deltaCol);

        switch (type) {
            case KING:
                // King moves exactly 1 square in any direction
                return absRow <= 1 && absCol <= 1 && (absRow != 0 || absCol != 0);

            case ROOK:
                // Rook moves horizontally or vertically, but not both
                return (absRow > 0 && absCol == 0) || (absRow == 0 && absCol > 0);

            case BISHOP:
                // Bishop moves diagonally (equal row and column change)
                return absRow == absCol && absRow > 0;

            case QUEEN:
                // Queen combines Rook and Bishop logic
                return ((absRow > 0 && absCol == 0) || (absRow == 0 && absCol > 0)) || (absRow == absCol && absRow > 0);

            case KNIGHT:
                // Knight moves in an L-shape (2x1 or 1x2)
                return (absRow == 2 && absCol == 1) || (absRow == 1 && absCol == 2);

            case PAWN:
                // Pawn movement is not required/validated for Iteration 3, return true/false based on target implementation
                return true;

            default:
                return false;
        }
    }


}
