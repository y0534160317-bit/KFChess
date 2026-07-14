package org.example.rules;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

public class RuleEngine {

    public boolean isValidMove(Board board, Position source, Position destination) {
        if (!board.isWithinBounds(destination) || source.equals(destination)) return false;

        Piece piece = board.getPiece(source);
        if (piece == null) return false;

        Piece targetPiece = board.getPiece(destination);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) return false;

        int rowDiff = destination.getRow() - source.getRow();
        int colDiff = destination.getCol() - source.getCol();
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        if (!new PieceRules().isValidMoveShape(piece.getType(), absRowDiff, absColDiff)) {
            return false;
        }

        // תיקון לוגיקת רגלי מלאה
        if (piece.getType() == Piece.Type.PAWN) {
            int direction = (piece.getColor() == Piece.Color.WHITE) ? -1 : 1;
            int startRow = (piece.getColor() == Piece.Color.WHITE) ? (board.getHeight() - 2) : 1;

            if (colDiff == 0) {
                if (targetPiece != null) return false;

                if (rowDiff == direction) {
                    return true;
                }
                if (rowDiff == 2 * direction && source.getRow() == startRow) {
                    int middleRow = source.getRow() + direction;
                    return board.getPiece(new Position(middleRow, source.getCol())) == null;
                }
                return false;
            } else if (absColDiff == 1 && rowDiff == direction) {
                return targetPiece != null && targetPiece.getColor() != piece.getColor();
            }
            return false;
        }

        // בדיקת מסלול נקי לכלים ארוכי טווח
        if (piece.getType() == Piece.Type.ROOK || piece.getType() == Piece.Type.BISHOP || piece.getType() == Piece.Type.QUEEN) {
            int rowStep = Integer.compare(destination.getRow(), source.getRow());
            int colStep = Integer.compare(destination.getCol(), source.getCol());

            int currRow = source.getRow() + rowStep;
            int currCol = source.getCol() + colStep;

            while (currRow != destination.getRow() || currCol != destination.getCol()) {
                if (board.getPiece(new Position(currRow, currCol)) != null) {
                    return false;
                }
                currRow += rowStep;
                currCol += colStep;
            }
        }

        return true;
    }

}