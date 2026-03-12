package de.rayzs.vit.api.event.events.player;

import de.rayzs.vit.api.event.Event;

/**
 * This event is called before asking the VALORANT servers
 * what the name of a user is.
 */
public class PreFetchPlayerNameEvent extends Event {

    private final String playerId;

    private boolean incognito;

    public PreFetchPlayerNameEvent(
            final String playerId,
            final boolean incognito
    ) {
        this.playerId = playerId;

        this.incognito = incognito;
    }

    /**
     * Set player incognito setting.
     * If enabled, then the name and level of the player will be hidden.
     *
     * @param incognito Is player incognito or not.
     */
    public void setIncognito(boolean incognito) {
        this.incognito = incognito;
    }

    /**
     * Get player id.
     *
     * @return ID of player.
     */
    public String getPlayerId() {
        return this.playerId;
    }

    /**
     * If incognito is enabled.
     *
     * @return True if incognito is enabled for the player. False otherwise.
     */
    public boolean isIncognito() {
        return this.incognito;
    }
}
