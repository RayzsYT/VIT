package de.rayzs.vit.launch;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.EventManager;
import de.rayzs.vit.api.image.ImageProvider;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.session.Session;

public class ImplVITAPI implements VITAPI {

    private final ImplEventManager eventManager;
    private final ImageProvider imageProvider;
    private final Session session;

    private Game game;
    private Weapon selectedWeapon = Weapon.VANDAL;

    public ImplVITAPI() {
        this.session = new ImplSession();
        this.imageProvider = new ImplImageProvider();
        this.eventManager = new ImplEventManager();
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public ImageProvider getImageProvider() {
        return this.imageProvider;
    }

    @Override
    public Game getGame() {
        if (this.game == null) {
            throw new NullPointerException("There's no running game!");
        }

        return this.game;
    }

    @Override
    public void setGame(final Game game) {
        this.game = game;
    }

    @Override
    public Weapon getSelectedWeapon() {
        if (this.selectedWeapon == null) {
            throw new NullPointerException("No weapon has been selected!");
        }

        return this.selectedWeapon;
    }

    @Override
    public void setSelectedWeapon(final Weapon selectedWeapon) {
        this.selectedWeapon = selectedWeapon;
    }
}
