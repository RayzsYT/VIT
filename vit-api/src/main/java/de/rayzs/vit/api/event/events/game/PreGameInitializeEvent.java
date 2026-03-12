package de.rayzs.vit.api.event.events.game;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called once the game has been initialisation process has started.
 * Can be a match in {@link SessionState#IN_LOBBY} or {@link SessionState#IN_GAME} state.
 */
public class PreGameInitializeEvent extends Event {

    private final SessionState state;
    private String server, mapName, mapId;

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
     * Set a new map id.
     *
     * @param mapId New map id.
     */
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    /**
     * Set a new map name.
     * Idk why someone would do that though since
     * it's not used anywhere. But perhaps in the future?
     *
     * @param mapName New map name.
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
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
