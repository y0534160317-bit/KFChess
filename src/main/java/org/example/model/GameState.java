package org.example.model;

public class GameState {
    private long gameTimeMillis;
    private boolean isGameOver;

    public GameState() {
        this.gameTimeMillis = 0;
        this.isGameOver = false;
    }

    public long getGameTimeMillis() { return gameTimeMillis; }
    public void advanceTime(long millis) { this.gameTimeMillis += millis; }

    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; }
}