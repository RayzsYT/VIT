package de.rayzs.vit.launch;

import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.event.EventListener;
import de.rayzs.vit.api.event.EventManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImplEventManager implements EventManager {

    private final Map<Addon, Set<EventListener<?>>> listeners = new HashMap<>();

    @Override
    public <E extends Event> void register(final Addon addon, final EventListener<E> listener) {
        listeners.computeIfAbsent(addon, k -> new HashSet<>()).add(listener);
    }

    @Override
    public void unregisterAll() {
        listeners.clear();
    }

    @Override
    public void unregisterAll(final Addon addon) {
        listeners.remove(addon);
    }

    @Override
    public <E extends Event> E call(final E event) {
        for (final Set<EventListener<?>> listeners : listeners.values()) {
            for (final EventListener<?> listener : listeners) {
                if (listener.type().isInstance(event)) {
                    ((EventListener<E>) listener).call(event);
                }
            }
        }

        return event;
    }
}
