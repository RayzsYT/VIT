package de.rayzs.vit.api;

import de.rayzs.vit.api.event.EventManager;
import de.rayzs.vit.api.image.ImageProvider;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.session.Session;

public interface VITAPI {

    /**
     * Get current VIT version.
     *
     * @return VIT version.
     */
    static String getVersion() { return "0.0.1"; }

    /**
     * Get the session to reload or fetch
     * VALORANT related information and create
     * a {@link Game} object.
     *
     * @return Session.
     */
    Session getSession();

    /**
     * Get the instance of the event manager.
     *
     * @return Event manager.
     */
    EventManager getEventManager();

    /**
     * Get the ImageProvider to create
     * or get DisplayImages.
     * 
     * @return ImageProvider.
     */
    ImageProvider getImageProvider();

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
