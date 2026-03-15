package de.rayzs.vit.launch.screens;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.launch.screens.game.elements.banners.PlayerBanner;
import de.rayzs.vit.launch.screens.game.elements.window.PlayerWindow;

import java.util.HashMap;
import java.util.Map;

public abstract class Screen {


    // Player Id, Value
    protected final Map<String, PlayerWindow> playerWindows = new HashMap<>();
    protected final Map<String, PlayerBanner> playerBanners = new HashMap<>();


    public void load(
            final VITAPI api,
            final MainGUI gui
    ) {
        // clears up player windows.
        playerWindows.values().forEach(PlayerWindow::dispose);
        playerWindows.clear();
        playerBanners.clear();


        gui.reset();
    }

    /**
     * Update certain information of a player banner.
     *
     * @param player New player object.
     */
    public void updatePlayerBanner(final Player player) {
        if (playerBanners.containsKey(player.id())) {
            playerBanners.get(player.id()).updatePlayer(player);
            return;
        }

        throw new NullPointerException("No player with id " + player.id() + " found! There are either no registered player banners, or the player you were trying to update is not in your match!");
    }

}
