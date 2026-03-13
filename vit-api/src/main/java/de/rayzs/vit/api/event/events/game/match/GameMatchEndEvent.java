package de.rayzs.vit.api.event.events.game.match;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.game.Game;

/**
 * Called when the {@link de.rayzs.vit.api.session.SessionState}
 * switched from {@link de.rayzs.vit.api.session.SessionState#IN_GAME}
 * to {@link de.rayzs.vit.api.session.SessionState#IN_MENU}.
 */
public class GameMatchEndEvent extends Event {

    private final Game game;

    public GameMatchEndEvent(final Game game) {
        this.game = game;
    }

    /**
     * The ended game.
     *
     * @return Ended game.
     */
    public Game getGame() {
        return this.game;
    }
}
