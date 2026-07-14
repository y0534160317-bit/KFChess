package org.example.model;

public class CoordinateMapper {

    public static final int CELL_SIZE = 100;

    /**
     * ממפה קואורדינטות מסך (פיקסלים) לאובייקט Position (שורה ועמודה בלוח)
     */
    public static Position toPosition(int x, int y, int componentWidth, int componentHeight) {
        // נניח שהלוח הוא תמיד 8x8
        int col = (x * 8) / componentWidth;
        int row = (y * 8) / componentHeight;
        return new Position(row, col);
    }
}