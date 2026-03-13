package de.rayzs.vit.api.event.events.game;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.items.MatchMap;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called once the game has been initialisation process has started.
 * Can be a match in {@link SessionState#IN_LOBBY} or {@link SessionState#IN_GAME} state.
 */
public class PreGameInitializeEvent extends Event {

    private final SessionState state;
    private String server;
    private MatchMap map;

    public PreGameInitializeEvent(
            final SessionState state,
            final String server,
            final MatchMap map
    ) {
        this.state = state;
        this.server = server;
        this.map = map;
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
     * Set a new map.
     *
     * @param map New map.
     */
    public void setMap(final MatchMap map) {
        this.map = map;
    }

    /**
     * Set server name
     *
     * @param server New server name.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Get connected server.
     *
     * @return Server.
     */
    public String getServer() {
        return this.server;
    }

    /**
     * Received map.
     *
     * @return Map.
     */
    public MatchMap getMap() {
        return this.map;
    }
}
