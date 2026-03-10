package de.rayzs.vit.api.event;

import de.rayzs.vit.api.addon.Addon;

public interface EventManager {

    /**
     * Register an event.
     *
     * @param addon Addon.
     * @param listener Event listener.
     */
    <E extends Event> void register(final Addon addon, final EventListener<E> listener);

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
    <E extends Event> E call(final E event);
}
