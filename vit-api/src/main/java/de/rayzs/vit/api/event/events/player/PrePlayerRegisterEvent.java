package de.rayzs.vit.api.event.events.player;

import de.rayzs.vit.api.event.Event;

/**
 * This event is called before asking the VALORANT servers
 * what the name of a user is.
 */
public class PrePlayerRegisterEvent extends Event {

    private final String playerId;

    private boolean incognito;
    private boolean hideLevels;

    public PrePlayerRegisterEvent(
            final String playerId,
            final boolean incognito,
            final boolean hideLevels
    ) {
        this.playerId = playerId;

        this.incognito = incognito;
        this.hideLevels = hideLevels;
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
     * Set visibility of player level.
     * If enabled, then the levels of the player will be set to -1.
     *
     * @param hideLevels IF player level is hidden.
     */
    public void setHideLevels(boolean hideLevels) {
        this.hideLevels = hideLevels;
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

    /**
     * If levels are hidden.
     *
     * @return True if levels are hidden for the player. False otherwise.
     */
    public boolean isHideLevels() {
        return this.hideLevels;
    }
}
