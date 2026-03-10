package de.rayzs.vit.api.event;

public class Event {

    /**
     * Default call method.
     * When creating an addon, just override
     * this to implement your own call.
     *
     * @param event Event.
     * @return Modified event.
     */
    public <E extends Event> E call(final E event) { return event; }

}
