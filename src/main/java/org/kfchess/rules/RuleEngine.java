package org.kfchess.rules;

import org.kfchess.model.Board;
import org.kfchess.model.BoardView;
import org.kfchess.model.Piece;
import org.kfchess.model.Position;

public class RuleEngine {

    public MoveValidationResult validateMove(BoardView board,
                                             Position source,
                                             Position destination) {

        if (!board.isWithinBounds(destination)) {
            return MoveValidationResult.invalid(
                    MoveValidationReason.OUT_OF_BOUNDS);
        }

        if (source.equals(destination)) {
            return MoveValidationResult.invalid(
                    MoveValidationReason.SAME_POSITION);
        }

        Piece piece = board.getPiece(source);
        if (piece == null) {
            return MoveValidationResult.invalid(MoveValidationReason.NO_PIECE);
        }

        Piece targetPiece = board.getPiece(destination);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return MoveValidationResult.invalid(MoveValidationReason.DESTINATION_OCCUPIED);
        }

        int rowDiff = destination.getRow() - source.getRow();
        int colDiff = destination.getCol() - source.getCol();
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        if (!new PieceRules().isValidMoveShape(piece.getType(), absRowDiff, absColDiff)) {
            return MoveValidationResult.invalid(MoveValidationReason.ILLEGAL_PATTERN);
        }

        // לוגיקת רגלי
        if (piece.getType() == Piece.Type.PAWN) {

            int direction = (piece.getColor() == Piece.Color.WHITE) ? -1 : 1;
            int startRow = (piece.getColor() == Piece.Color.WHITE)
                    ? (board.getHeight() - 2)
                    : 1;

            if (colDiff == 0) {

                if (targetPiece != null) {
                    return MoveValidationResult.invalid(
                            MoveValidationReason.DESTINATION_OCCUPIED);
                }

                if (rowDiff == direction) {
                    return MoveValidationResult.valid();
                }

                if (rowDiff == 2 * direction && source.getRow() == startRow) {

                    int middleRow = source.getRow() + direction;

                    if (board.getPiece(new Position(middleRow, source.getCol())) == null) {
                        return MoveValidationResult.valid();
                    }

                    return MoveValidationResult.invalid(
                            MoveValidationReason.PATH_BLOCKED);
                }

                return MoveValidationResult.invalid(
                        MoveValidationReason.ILLEGAL_PATTERN);

            } else if (absColDiff == 1 && rowDiff == direction) {

                if (targetPiece != null &&
                        targetPiece.getColor() != piece.getColor()) {

                    return MoveValidationResult.valid();
                }

                return MoveValidationResult.invalid(
                        MoveValidationReason.ILLEGAL_PATTERN);
            }

            return MoveValidationResult.invalid(
                    MoveValidationReason.ILLEGAL_PATTERN);
        }

        // בדיקת מסלול לכלים ארוכי טווח
        if (piece.getType() == Piece.Type.ROOK
                || piece.getType() == Piece.Type.BISHOP
                || piece.getType() == Piece.Type.QUEEN) {

            int rowStep = Integer.compare(destination.getRow(), source.getRow());
            int colStep = Integer.compare(destination.getCol(), source.getCol());

            int currRow = source.getRow() + rowStep;
            int currCol = source.getCol() + colStep;

            while (currRow != destination.getRow()
                    || currCol != destination.getCol()) {

                if (board.getPiece(new Position(currRow, currCol)) != null) {

                    return MoveValidationResult.invalid(
                            MoveValidationReason.PATH_BLOCKED);
                }

                currRow += rowStep;
                currCol += colStep;
            }
        }

        return MoveValidationResult.valid();
    }

}