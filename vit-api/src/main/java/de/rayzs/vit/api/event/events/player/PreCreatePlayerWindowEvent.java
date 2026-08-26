package de.rayzs.vit.api.event.events.player;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.player.Player;

/**
 * This event is called before the player window
 * of a player is generated.
 */
public class PreCreatePlayerWindowEvent extends Event {

    private Player player;

    public PreCreatePlayerWindowEvent(final Player player) {
        this.player = player;
    }

    /**
     * Replace current player with a new player.
     *
     * @param player New player object.
     */
    public void setPlayer(final Player player) {
        this.player = player;
    }

    /**
     * Return current event player object.
     *
     * @return Current event player object.
     */
    public Player getPlayer() {
        return player;
    }
}
