package de.rayzs.vit.api.event.events.game.match;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.event.events.game.GameInitializedEvent;
import de.rayzs.vit.api.objects.game.Game;

/**
 * Called after {@link GameInitializedEvent} once the actual match
 * started. This event also decides whether a game should be saved or not.
 * Only called when the {@link de.rayzs.vit.api.session.SessionState}
 * is set to {@link de.rayzs.vit.api.session.SessionState#IN_GAME}.
 */
public class GameMatchStartEvent extends Event {

    private final Game game;
    private boolean saveGame;

    public GameMatchStartEvent(final Game game, final boolean saveGame) {
        this.game = game;
        this.saveGame = saveGame;
    }

    /**
     * Should VIT save the game?
     *
     * @return Boolean indicating whether to save the game or not.
     */
    public boolean doesSaveGame() {
        return this.saveGame;
    }

    /**
     * Set whether game should be saved or not.
     *
     * @param saveGame Boolean whether the game should be saved or not.
     */
    public void setSaveGame(final boolean saveGame) {
        this.saveGame = saveGame;
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
