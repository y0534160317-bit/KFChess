package org.example.view;

public class PieceVisualState {

    private final AnimationState state;
    private final long stateStartTime;

    public PieceVisualState(AnimationState state, long stateStartTime) {
        this.state = state;
        this.stateStartTime = stateStartTime;
    }

    public AnimationState getState() {
        return state;
    }

    public long getStateStartTime() {
        return stateStartTime;
    }
}