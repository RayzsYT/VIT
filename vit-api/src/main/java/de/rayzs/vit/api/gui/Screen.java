package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.objects.player.Player;

public interface Screen {

    /**
     * Update certain information of a player banner.
     *
     * @param player New player object.
     */
    void updatePlayerBanner(final Player player);
}
