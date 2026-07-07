package org.example;

public class Board {
    private final int width;
    private final int height;
    private final Piece[][] grid; // Dynamic 2D array to hold the board state

    // Initializes the board dynamically based on inferred dimensions
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Piece[height][width]; // Rows come first (height), then columns (width)
    }

    // Places a piece at a specific coordinate on the grid
    public void setPiece(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    // Prints the board configuration precisely as expected by the VPL platform
    public void print() {
        for (int r = 0; r < height; r++) {
            StringBuilder rowStr = new StringBuilder();
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == null) {
                    rowStr.append("."); // Prints a dot for empty squares
                } else {
                    rowStr.append(grid[r][c].toString()); // Prints the piece token (e.g., "wK")
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