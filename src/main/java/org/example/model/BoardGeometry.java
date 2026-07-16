package org.example.model;


public final class BoardGeometry {

    private final int x;
    private final int y;
    private final int size;

    public BoardGeometry(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}