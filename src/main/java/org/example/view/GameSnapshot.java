package org.example.view;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.realtime.ActiveMotion;
import org.example.realtime.RealTimeArbiter;

import java.util.List;
import java.util.Map;

public class GameSnapshot {
    private final Board board;
    private final List<ActiveMotion> activeMotions;
    private final Position selectedPosition;
    private final long currentTimeMillis;
    private final Map<Piece, PieceVisualState> visualStates;

    public GameSnapshot(
            Board board,
            List<ActiveMotion> activeMotions,
            Position selectedPosition,
            long currentTimeMillis,
            Map<Piece, PieceVisualState> visualStates) {

        this.board = board;
        this.activeMotions = activeMotions;
        this.selectedPosition = selectedPosition;
        this.currentTimeMillis = currentTimeMillis;
        this.visualStates = visualStates;
    }

    // Getters למען ה-Renderer
    public Board getBoard() { return board; }
    public List<ActiveMotion> getActiveMotions() { return activeMotions; }
    public Position getSelectedPosition() { return selectedPosition; }
    public long getCurrentTimeMillis() { return currentTimeMillis; }
    public Map<Piece, PieceVisualState> getVisualStates() { return visualStates; }

}