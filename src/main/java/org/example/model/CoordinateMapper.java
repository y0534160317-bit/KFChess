package org.example.model;

public class CoordinateMapper {

    public static final int CELL_SIZE = 100;

    /**
     * ממפה קואורדינטות מסך (פיקסלים) לאובייקט Position (שורה ועמודה בלוח)
     */
    public static Position toPosition(int x, int y) {
        int row = y / CELL_SIZE;
        int col = x / CELL_SIZE;
        return new Position(row, col);
    }
}