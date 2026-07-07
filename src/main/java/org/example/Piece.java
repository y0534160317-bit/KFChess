package org.example;

public class Piece {

    // Represents the color of the chess piece
    public enum Color {
        WHITE('w'), BLACK('b');

        private final char symbol;

        Color(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        // Safely converts a character input to a Color enum
        public static Color fromChar(char c) {
            if (c == 'w') return WHITE;
            if (c == 'b') return BLACK;
            return null; // Returns null if the character is invalid
        }
    }

    // Represents the type of the chess piece
    public enum Type {
        KING('K'), QUEEN('Q'), ROOK('R'), KNIGHT('N'), BISHOP('B'), PAWN('P');

        private final char symbol;

        Type(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        // Iterates through all enum values to find a match for the input character
        public static Type fromChar(char c) {
            for (Type t : Type.values()) {
                if (t.symbol == c) return t;
            }
            return null; // Returns null if no matching chess piece is found
        }
    }

    private final Color color;
    private final Type type;

    // Constructor to initialize a piece with its color and type
    public Piece(Color color, Type type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    // Reconstructs the canonical string representation (e.g., "wK", "bQ")
    @Override
    public String toString() {
        return "" + color.getSymbol() + type.getSymbol();
    }
}