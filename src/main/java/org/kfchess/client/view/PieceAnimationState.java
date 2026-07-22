package org.kfchess.client.view;

public class PieceAnimationState {

    private AnimationState state;
    private long stateStartTime;

    public PieceAnimationState() {
        state = AnimationState.IDLE;
        stateStartTime = 0;
    }

    public AnimationState getState() {
        return state;
    }

    public long getStateStartTime() {
        return stateStartTime;
    }

    public void setState(AnimationState state, long currentTime) {
        this.state = state;
        this.stateStartTime = currentTime;
    }
}
