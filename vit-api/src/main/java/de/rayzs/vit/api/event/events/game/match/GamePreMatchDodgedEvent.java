package de.rayzs.vit.api.event.events.game.match;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.game.Game;

/**
 * Called once the lobby, so the agent selection, has been dodged.
 * When the {@link de.rayzs.vit.api.session.SessionState}
 * switched from {@link de.rayzs.vit.api.session.SessionState#IN_LOBBY}
 * to {@link de.rayzs.vit.api.session.SessionState#IN_MENU}.
 */
public class GamePreMatchDodgedEvent extends Event {

    private final Game game;

    public GamePreMatchDodgedEvent(final Game game) {
        this.game = game;
    }

    /**
     * The dodged pre-game.
     *
     * @return Dodged pre-game.
     */
    public Game getGame() {
        return this.game;
    }
}
