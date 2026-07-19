package org.kfchess.view;

import org.kfchess.model.Board;
import org.kfchess.model.BoardView;
import org.kfchess.model.Piece;
import org.kfchess.model.Position;
import org.kfchess.realtime.ActiveMotion;

import java.util.List;
import java.util.Map;

public class GameSnapshot {
    private final BoardView board;    private final List<ActiveMotion> activeMotions;
    private final Position selectedPosition;
    private final long currentTimeMillis;
    private final Map<Piece, PieceVisualState> visualStates;
    private final int whiteScore;
    private final int blackScore;

    public GameSnapshot(
            BoardView board,
            List<ActiveMotion> activeMotions,
            Position selectedPosition,
            long currentTimeMillis,
            Map<Piece, PieceVisualState> visualStates,
            int whiteScore,
            int blackScore) {

        this.board = board;
        this.activeMotions = activeMotions;
        this.selectedPosition = selectedPosition;
        this.currentTimeMillis = currentTimeMillis;
        this.visualStates = visualStates;
        this.whiteScore = whiteScore;
        this.blackScore = blackScore;
    }

    // Getters למען ה-Renderer
    public BoardView getBoard() { return board; }
    public List<ActiveMotion> getActiveMotions() { return activeMotions; }
    public Position getSelectedPosition() { return selectedPosition; }
    public long getCurrentTimeMillis() { return currentTimeMillis; }
    public Map<Piece, PieceVisualState> getVisualStates() { return visualStates; }
    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

}