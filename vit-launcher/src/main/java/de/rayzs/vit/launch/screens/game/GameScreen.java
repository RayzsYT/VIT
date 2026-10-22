package de.rayzs.vit.launch.screens.game;

import de.rayzs.vit.api.objects.player.Player;

public interface GameScreen {

    /**
     * Open a player window at the exact location.
     *
     * @param player Player.
     * @param x X location.
     * @param y Y location.
     */
    void openPlayerWindow(final Player player, final int x, final int y);
}
