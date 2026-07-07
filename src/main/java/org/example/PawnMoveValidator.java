package org.example;

import java.util.List;

public class PawnMoveValidator {

    // Validates pawn moves based on color, direction, starting rows, path clearance, and targets
    public static boolean isValidPawnMove(Position from, Position to, Piece pawn, Board board, List<ActiveMove> activeMoves) {

        int deltaRow = to.getRow() - from.getRow();
        int deltaCol = to.getCol() - from.getCol();
        int absCol = Math.abs(deltaCol);

        // Determine direction based on color: White moves up (-1), Black moves down (+1)
        int direction = (pawn.getColor() == Piece.Color.WHITE) ? -1 : 1;

        int startingRow = (pawn.getColor() == Piece.Color.WHITE) ? (board.getHeight() - 1) : 0;
        Piece targetPiece = board.getPiece(to);

        // Case 1: Forward movement (no column change)
        if (deltaCol == 0) {
            // Target square must be empty statically and dynamically
            if (targetPiece != null || isSquareOccupiedByActiveMove(to, pawn.getColor(), activeMoves)) {
                return false;
            }

            // Sub-case 1A: Standard 1-square forward move
            if (deltaRow == direction) {
                return true;
            }

            // Sub-case 1B: Advanced 2-square forward move from starting row
            if (deltaRow == 2 * direction && from.getRow() == startingRow) {
                // Calculate the middle square position
                Position middlePos = new Position(from.getRow() + direction, from.getCol());

                // Middle square must be completely clear (static piece check + dynamic active move check)
                if (board.getPiece(middlePos) != null || isSquareOccupiedByActiveMove(middlePos, pawn.getColor(), activeMoves)) {
                    return false;
                }
                return true;
            }

            return false;
        }

        // Case 2: Diagonal capture (exactly 1 column away and 1 row forward)
        if (absCol == 1 && deltaRow == direction) {
            return targetPiece != null && targetPiece.getColor() != pawn.getColor();
        }

        // Any other move (e.g., moving 2+ columns sideways) is invalid
        return false;
    }

    // Helper to check if a specific square is a future landing spot for a friendly moving piece
    private static boolean isSquareOccupiedByActiveMove(Position pos, Piece.Color movingColor, List<ActiveMove> activeMoves) {
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos) && move.getPiece().getColor() == movingColor) {
                return true;
            }
        }
        return false;
    }
}