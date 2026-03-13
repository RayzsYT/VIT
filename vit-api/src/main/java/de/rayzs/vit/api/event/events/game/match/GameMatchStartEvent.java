package de.rayzs.vit.api.event.events.game.match;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.event.events.game.GameInitializedEvent;
import de.rayzs.vit.api.objects.game.Game;

/**
 * Called after {@link GameInitializedEvent} once the actual match
 * started. Only when the {@link de.rayzs.vit.api.session.SessionState}
 * is set to {@link de.rayzs.vit.api.session.SessionState#IN_GAME}.
 */
public class GameMatchStartEvent extends Event {

    private final Game game;

    public GameMatchStartEvent(final Game game) {
        this.game = game;
    }

    /**
     * The current game.
     *
     * @return Current game.
     */
    public Game getGame() {
        return this.game;
    }
}
