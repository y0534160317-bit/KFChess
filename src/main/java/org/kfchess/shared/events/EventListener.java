package org.kfchess.shared.events;

public interface EventListener<T extends Event> {

    void onEvent(T event);

}