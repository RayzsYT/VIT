package de.rayzs.vit.api.event;

public abstract class EventAdapter<E extends Event> implements EventListener<E> {

    private final Class<E> eventClass;

    public EventAdapter(final Class<E> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public Class<E> type() {
        return eventClass;
    }
}
