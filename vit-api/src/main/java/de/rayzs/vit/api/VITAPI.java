package de.rayzs.vit.api;

import de.rayzs.vit.api.addon.AddonManager;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.event.EventManager;
import de.rayzs.vit.api.gui.Screen;
import de.rayzs.vit.api.image.ImageProvider;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.session.Session;
import de.rayzs.vit.api.session.SessionState;

import java.text.SimpleDateFormat;

public interface VITAPI {


    // Time and date formats, so I don't have to copy-paste them across the entire project.
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy-HH");
    SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");


    /**
     * Get current VIT version.
     *
     * @return VIT version.
     */
    static String getVersion() { return "1.0.12"; }

    /**
     * Get current screen.
     *
     * @return Current screen.
     */
    Screen getCurrentScreen();

    /**
     * Set current screen. Not intended to be used
     * by any other third-party software!
     *
     * @param screen New current screen.
     */
    void setCurrentScreen(final Screen screen);

    /**
     * Default VIT settings.
     *
     * @return Default VIT settings.
     */
    Configuration getSettings();

    /**
     * Get the session to reload or fetch
     * VALORANT related information and create
     * a {@link Game} object.
     *
     * @return Session.
     */
    Session getSession();

    /**
     * Get current session state.
     *
     * @return Current session state.
     */
    SessionState getSessionState();

    /**
     * Update session state.
     *
     * @param state New session state.
     */
    void updateSessionState(final SessionState state);

    /**
     * Update array of all agents the current
     * instance owns. Can only be done once!
     */
    void updateOwningAgents(Agent... agents);

    /**
     * Array of all agents the current instance
     * owns and can play.
     *
     * @return Array of all owning agents.
     */
    Agent[] getOwningAgents();

    /**
     * Get the instance of the event manager.
     *
     * @return Event manager.
     */
    EventManager getEventManager();

    /**
     * Get the instance of the addon manager.
     *
     * @return Addon manager.
     */
    AddonManager getAddonManager();

    /**
     * Get the ImageProvider to create
     * or get DisplayImages.
     * 
     * @return ImageProvider.
     */
    ImageProvider getImageProvider();

    /**
     * If a {@link Game} instance exists.
     *
     * @return True if it exists. False otherwise.
     */
    boolean hasGame();

    /**
     * Get the current game
     * which is being played.
     *
     * @return Game being played.
     */
    Game getGame();

    /**
     * Set the game being played.
     *
     * @param game Game being played.
     */
    void setGame(final Game game);

    /**
     * Get the currently selected weapon being
     * shown by default on the live gui.
     *
     * @return Weapon.
     */
    Weapon getSelectedWeapon();

    /**
     * Set the selected weapon which is being
     * shown by default on the live gui.
     *
     * @param weapon Weapon.
     */
    void setSelectedWeapon(final Weapon weapon);
}
