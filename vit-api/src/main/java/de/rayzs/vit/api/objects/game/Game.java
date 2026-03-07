package de.rayzs.vit.api.objects.game;

import de.rayzs.vit.api.objects.player.Player;

public record Game(
        Player self,
        GameState gameState,
        Player[] players,
        String mapId,
        String server
) {


    /**
     * Get the id of the map
     * being played on.
     *
     * @return Id of the map being played on.
     */
    @Override
    public String mapId() {
        return this.mapId;
    }

    /**
     * Get the player who is
     * using the program.
     *
     * @return Player.
     */
    @Override
    public Player self() {
        return this.self;
    }

    /**
     * Get an array of all players.
     *
     * @return Array of all players.
     */
    @Override
    public Player[] players() {
        return this.players;
    }

    /**
     * Get the current state of the game,
     * if the client is in the lobby/agent selection,
     * or already inside the match.
     *
     * @return State of the game.
     */
    @Override
    public GameState gameState() {
        return gameState;
    }
}
