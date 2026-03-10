package de.rayzs.vit.api.event.events;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.session.SessionState;

/**
 * Called once the game has been initialised.
 * Can be a match in {@link SessionState#IN_LOBBY} or {@link SessionState#IN_GAME} state.
 */
public class GameInitializedEvent extends Event {

    private final SessionState state;
    private final Game game;

    public GameInitializedEvent(
            final SessionState state,
            final Game game
    ) {
        this.state = state;
        this.game = game;
    }

    /**
     * Get session state.
     *
     * @return Session state.
     */
    public SessionState getState() {
        return this.state;
    }

    /**
     * Get initialized game object.
     *
     * @return Initialized game object.
     */
    public Game getGame() {
        return this.game;
    }
}
