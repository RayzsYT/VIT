package de.rayzs.vit.api.addon.event;

import de.rayzs.vit.api.addon.Addon;

public interface EventManager {

    /**
     * Register an event.
     *
     * @param addon Addon.
     * @param event Event.
     */
    void register(final Addon addon, final Event event);

    /**
     * Unregister all registered events.
     */
    void unregisterAll();

    /**
     * Unregisters all events from
     * an addon.
     *
     * @param addon Addon.
     */
    void unregisterAll(final Addon addon);

    /**
     * Call the event and iterates through each
     * and every listener waiting for the event.
     *
     * @param event Event.
     * @return Returns modified version of {@link Event} if any of its arguments have been modified.
     */
    Event call(final Event event);
}
