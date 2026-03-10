package de.rayzs.vit.launch;

import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.event.EventManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManagerImpl implements EventManager {

    private final Map<Addon, Set<Event>> events = new HashMap<>();

    @Override
    public void register(final Addon addon, final Event event) {
        events.computeIfAbsent(addon, k -> new HashSet<>()).add(event);
    }

    @Override
    public void unregisterAll() {
        events.clear();
    }

    @Override
    public void unregisterAll(final Addon addon) {
        events.remove(addon);
    }

    @Override
    public <E extends Event> E call(final E event) {
        for (final Set<Event> events : events.values()) {
            for (final Event e : events) {
                e.call(event);
            }
        }

        return event;
    }
}
