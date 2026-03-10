package de.rayzs.vit.api.event;

public interface EventListener<E extends Event> {

    /**
     * Class type of the event.
     *
     * @return Event class type.
     */
    Class<E> type();

    /**
     * Default call method.
     * When creating an addon, just override
     * this to implement your own call.
     *
     * @param event Event.
     * @return Modified event.
     */
    E call(E event);
}
