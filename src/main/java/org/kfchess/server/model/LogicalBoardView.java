package org.kfchess.server.model;



import org.kfchess.server.realtime.ActiveMotion;
import org.kfchess.shared.model.BoardView;
import org.kfchess.shared.model.Piece;
import org.kfchess.shared.model.Position;

import java.util.List;

public class LogicalBoardView implements BoardView {

    private final BoardView board;
    private final List<ActiveMotion> activeMotions;

    public LogicalBoardView(
            BoardView board,
            List<ActiveMotion> activeMotions) {

        this.board = board;
        this.activeMotions = activeMotions;
    }

    @Override
    public Piece getPiece(Position position) {

        for (ActiveMotion motion : activeMotions) {

            if (motion.isCancelled()) {
                continue;
            }

            if (motion.getSource().equals(position)) {
                return null;
            }
        }

        return board.getPiece(position);
    }

    @Override
    public int getWidth() {
        return board.getWidth();
    }

    @Override
    public int getHeight() {
        return board.getHeight();
    }

    @Override
    public boolean isWithinBounds(Position position) {
        return board.isWithinBounds(position);
    }

    @Override
    public Position findPiece(Piece piece) {

        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {

                Position pos = new Position(row, col);

                if (piece.equals(getPiece(pos))) {
                    return pos;
                }
            }
        }

        return null;
    }

    /**
     * מחזיר true אם קיימת תנועה פעילה של כלי מהצבע הנתון
     * שהיעד שלה הוא המשבצת המבוקשת.
     */
    public boolean isReserved(Position position, Piece.Color color) {

        for (ActiveMotion motion : activeMotions) {

            // מתעלמים מתנועות שכבר בוטלו
            if (motion.isCancelled()) {
                continue;
            }

            // בודקים האם התנועה מיועדת למשבצת הזו
            if (!motion.getDestination().equals(position)) {
                continue;
            }

            // בודקים שהכלי הוא מאותו צבע
            if (motion.getPiece().getColor() != color) {
                continue;
            }

            // מצאנו תנועה פעילה של כלי מאותו צבע לאותו יעד
            return true;
        }

        return false;
    }
}