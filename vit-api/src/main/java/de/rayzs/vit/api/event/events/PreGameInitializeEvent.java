package de.rayzs.vit.api.event.events;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called once the game has been initialisation process has started.
 * Can be a match in {@link SessionState#IN_LOBBY} or {@link SessionState#IN_GAME} state.
 */
public class PreGameInitializeEvent extends Event {

    private final SessionState state;
    private final String server, mapName, mapId;

    public PreGameInitializeEvent(
            final SessionState state,
            final String server,
            final String mapName,
            final String mapId
    ) {
        this.state = state;
        this.server = server;
        this.mapName = mapName;
        this.mapId = mapId;
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
     * Get connected server.
     *
     * @return Server.
     */
    public String getServer() {
        return this.server;
    }

    /**
     * Received map name.
     *
     * @return Map name.
     */
    public String getMapName() {
        return this.mapName;
    }

    /**
     * Received map id.
     *
     * @return Map id.
     */
    public String getMapId() {
        return this.mapId;
    }
}
