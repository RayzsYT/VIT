package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;

public record Player(
        Team team,                      // Player team
        String name,                    // Player full name
        int level,                      // Player level
        String playerCardId,            // Player card id
        String playerTitleId,           // Player title id
        PlayerInventory inventory,      // Player inventory
        Agent agent,                    // Player agent
        PlayerCompetitive competitive   // Player stored competitive information
) {

    /**
     * Create a Player object.
     *
     * @param team Player team.
     * @param name Full player name & tag.
     * @param level Player level.
     * @param playerCardId Player card id.
     * @param playerTitleId Player title id.
     * @param agent Player agent.
     * @param competitive Competitive information about the player.
     *
     * @return Created Player.
     */
    public static Player createPlayer(
            final Team team,
            final String name,
            final int level,
            final String playerCardId,
            final String playerTitleId,
            final PlayerInventory inventory,
            final Agent agent,
            final PlayerCompetitive competitive
    ) {
        return new Player(
                team, name, level, playerCardId, playerTitleId,
                inventory, agent, competitive
        );
    }

    /**
     * Create a Player object for the lobby.
     * Basically just the object but without
     * an agent or team.
     *
     * @param team Player team.
     * @param name Full player name & tag.
     * @param level Player level.
     * @param playerCardId Player card id.
     * @param playerTitleId Player title id.
     *
     * @return Created Player.
     */
    public static Player createLobbyPlayer(
            final Team team,
            final String name,
            final int level,
            final String playerCardId,
            final String playerTitleId
    ) {
        return new Player(
                team, name, level, playerCardId, playerTitleId,
                null, null, null
        );
    }
}
