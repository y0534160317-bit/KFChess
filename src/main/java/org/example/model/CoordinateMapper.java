package org.example.model;

public class CoordinateMapper {

    public static final int CELL_SIZE = 100;
    public static Position toPosition(
            int x, int y, BoardGeometry geometry) {


        if (geometry == null) {
            return null;
        }

        int boardX = geometry.getX();
        int boardY = geometry.getY();
        int boardSize = geometry.getSize();
        // האם הלחיצה בכלל בתוך הלוח?
        if (x < boardX || x >= boardX + boardSize ||
                y < boardY || y >= boardY + boardSize) {
            return null;
        }

        // מעבירים את נקודת ההתחלה של הלוח ל-(0,0)
        int localX = x - boardX;
        int localY = y - boardY;

        int col = (localX * 8) / boardSize;
        int row = (localY * 8) / boardSize;

        return new Position(row, col);
    }
}