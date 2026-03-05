package de.rayzs.vit.api.objects.game;

import de.rayzs.vit.api.objects.player.Player;

public class Game {

    private final Player self;

    private final Player[] players;
    private final String mapId;

    public Game(
            final Player self,
            final Player[] players,
            final String mapId
    ) {
        this.self = self;
        this.players = players;
        this.mapId = mapId;
    }


    /**
     * Get the id of the map
     * being played on.
     *
     * @return Id of the map being played on.
     */
    public String getMapId() {
        return this.mapId;
    }

    /**
     * Get the player who is
     * using the program.
     *
     * @return Player.
     */
    public Player getSelf() {
        return this.self;
    }

    /**
     * Get an array of all players.
     *
     * @return Array of all players.
     */
    public Player[] getPlayers() {
        return this.players;
    }
}
