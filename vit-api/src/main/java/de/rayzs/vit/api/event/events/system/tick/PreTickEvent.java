package de.rayzs.vit.api.event.events.system.tick;

import de.rayzs.vit.api.event.Cancellable;
import de.rayzs.vit.api.event.Event;

/**
 * Called on each tick before new VALORANT related information is being fetched.
 * Therefore, this event is called before {@link TickEvent}.
 * The information being checked determines the new {@link de.rayzs.vit.api.session.SessionState}.
 */
public class PreTickEvent extends Event implements Cancellable {

    private boolean cancelled = false;

    /**
     * Set if the VALORANT related information can be set or not.
     * If cancelled, then VALORANT related information won't be fetched.
     *
     * @param cancelled If VALORANT related information can be fetched or not.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * If cancelled, then tick to fetch VALORANT related information
     * won't proceed.
     *
     * @return If VALORANT information can be fetched or not.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
