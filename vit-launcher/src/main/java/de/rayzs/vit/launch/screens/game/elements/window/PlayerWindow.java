package de.rayzs.vit.launch.screens.game.elements.window;

import de.rayzs.vit.api.objects.player.Player;

public interface PlayerWindow {

    /**
     * Close player window entirely.
     */
    void dispose();

    /**
     * Show the player window at the mouse
     * coordination.
     *
     * @param x Mouse x location.
     * @param y Mouse y location.
     */
    void show(final int x, final int y);

    /**
     * Shortens a player name to a constant size.
     * Used for names to guarantee that longs names
     * don't move the buttons.
     *
     * @param player Player.
     *
     * @return Shorten player name.
     */
    default String shortenPlayerName(final Player player) {
        final String name = player.name();
        final int max = 18;

        if (name.length() <= max)
            return name;

        return name.substring(0, max) + "...";
    }
}
