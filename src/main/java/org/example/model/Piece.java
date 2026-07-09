package org.example.model;

public class Piece {

    public enum Color {
        WHITE('w'), BLACK('b');

        private final char symbol;

        Color(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static Color fromChar(char c) {
            if (c == 'w') return WHITE;
            if (c == 'b') return BLACK;
            return null;
        }
    }

    public enum Type {
        KING('K'), QUEEN('Q'), ROOK('R'), KNIGHT('N'), BISHOP('B'), PAWN('P');

        private final char symbol;

        Type(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static Type fromChar(char c) {
            for (Type t : Type.values()) {
                if (t.symbol == c) return t;
            }
            return null;
        }
    }

    private final Color color;
    private final Type type;

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

    @Override
    public String toString() {
        return "" + color.getSymbol() + type.getSymbol();
    }
}