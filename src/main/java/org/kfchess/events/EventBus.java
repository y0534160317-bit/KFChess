package org.kfchess.events;

import java.util.*;

public class EventBus {

    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners =
            new HashMap<>();

    public <T extends Event> void subscribe(
            Class<T> eventType,
            EventListener<T> listener) {

        listeners
                .computeIfAbsent(eventType, key -> new ArrayList<>())
                .add(listener);
    }

    public <T extends Event> void unsubscribe(
            Class<T> eventType,
            EventListener<T> listener) {

        List<EventListener<? extends Event>> eventListeners =
                listeners.get(eventType);

        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void publish(T event) {

        List<EventListener<? extends Event>> eventListeners =
                listeners.get(event.getClass());

        if (eventListeners == null) {
            return;
        }

        for (EventListener<? extends Event> listener : eventListeners) {
            ((EventListener<T>) listener).onEvent(event);
        }
    }
}