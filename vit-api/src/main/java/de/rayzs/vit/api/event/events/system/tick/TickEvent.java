package de.rayzs.vit.api.event.events.system.tick;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called on each tick after the new VALORANT related information has been fetched.
 * Therefore, this event is called after {@link PreTickEvent}.
 * The fetched information determines the new {@link de.rayzs.vit.api.session.SessionState}.
 */
public class TickEvent extends Event {

    private final SessionState state;

    public TickEvent(final SessionState state) {
        this.state = state;
    }

    /**
     * Fetched session state.
     *
     * @return Session state.
     */
    public SessionState getState() {
        return state;
    }
}
