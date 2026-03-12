package de.rayzs.vit.api.event.events.gui;

import de.rayzs.vit.api.event.Cancellable;
import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.event.events.system.state.StateChangeEvent;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called every time the session state changes.
 * Is called after {@link StateChangeEvent}.
 */
public class UpdateMainGuiEvent extends Event implements Cancellable {

    private final SessionState state;
    private final MainGUI gui;

    private boolean cancelled = false;

    public UpdateMainGuiEvent(
            final SessionState state,
            final MainGUI gui
    ) {
       this.state = state;
       this.gui = gui;
    }

    /**
     * Set if the main gui can update or not.
     * If cancelled, then the main gui won't update.
     *
     * @param cancelled If main gui can update.
     */
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get main gui.
     *
     * @return Main gui.
     */
    public MainGUI getGui() {
        return this.gui;
    }

    /**
     * Get session state.
     *
     * @return Session state.
     */
    public SessionState getState() {
        return this.state;
    }

    /**
     * If this event is cancelled.
     * If cancelled, then the main GUI won't update.
     *
     * @return If the main gui can update or not.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
