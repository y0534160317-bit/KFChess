package org.kfchess.events;

public interface MoveObserver {

    void onMoveCompleted(MoveEvent event);

}
