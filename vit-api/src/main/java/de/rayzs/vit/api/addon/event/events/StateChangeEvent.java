package de.rayzs.vit.api.addon.event.events;

import de.rayzs.vit.api.addon.event.Event;
import de.rayzs.vit.api.session.SessionState;

/**
 * Whenever the state of VALORANT changes.
 * For example from {@link SessionState#IN_LOBBY} to {@link SessionState#IN_GAME}.
 */
public class StateChangeEvent extends Event {

    private final SessionState before, state;

    public StateChangeEvent(final SessionState before, final SessionState state) {
        this.before = before;
        this.state = state;
    }

    /**
     * Get the old state which it changed from.
     *
     * @return Previous state.
     */
    public SessionState getOldState() {
        return this.before;
    }

    /**
     * Get new state.
     *
     * @return New state.
     */
    public SessionState getState() {
        return this.state;
    }
}
