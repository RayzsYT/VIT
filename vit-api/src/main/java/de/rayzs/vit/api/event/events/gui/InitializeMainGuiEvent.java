package de.rayzs.vit.api.event.events.gui;

import de.rayzs.vit.api.event.Cancellable;
import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.gui.GUI;

/**
 * Called once when the gui is initialized for the very first time.
 */
public class InitializeMainGuiEvent extends Event implements Cancellable {

    private final GUI gui;

    private boolean cancelled = false;

    public InitializeMainGuiEvent(
            final GUI gui
    ) {
       this.gui = gui;
    }

    /**
     * Set if main gui can be initialized or not.
     * If cancelled, then the main gui won't pop up anymore
     * and the {@link UpdateMainGuiEvent} won't be called.
     *
     * @param cancelled If main gui can be initialized or not.
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
    public GUI getGui() {
        return this.gui;
    }

    /**
     * If the main gui can be initialized or not.
     *
     * @return If the main gui can be initialized.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
