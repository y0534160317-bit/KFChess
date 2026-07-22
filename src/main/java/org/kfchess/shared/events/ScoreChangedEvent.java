package org.kfchess.shared.events;

public class ScoreChangedEvent implements Event {

    private final int whiteScore;
    private final int blackScore;

    public ScoreChangedEvent(int whiteScore, int blackScore) {
        this.whiteScore = whiteScore;
        this.blackScore = blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }
}