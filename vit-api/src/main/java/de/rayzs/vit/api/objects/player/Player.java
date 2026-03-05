package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Tier;

public record Player(
        String name,
        int level,
        String playerCardId,
        String playerTitleId,
        PlayerInventory inventory,
        Team team,
        Agent agent,
        Tier currentTier,
        Tier peakTier
) {

    /**
     * Create a Player object.
     *
     * @param name Full player name & tag.
     * @param level Player level.
     * @param playerCardId Player card id.
     * @param playerTitleId Player title id.
     * @param team Player team.
     * @param agent Player agent.
     * @param currentTier Player current rank.
     * @param peakTier Player peak rank.
     *
     * @return Created Player.
     */
    public static Player createPlayer(
            final String name,
            final int level,
            final String playerCardId,
            final String playerTitleId,
            final PlayerInventory inventory,
            final Team team,
            final Agent agent,
            final Tier currentTier,
            final Tier peakTier
    ) {
        return new Player(
                name, level, playerCardId, playerTitleId,
                inventory, team, agent, currentTier, peakTier
        );
    }

    /**
     * Create a Player object for the lobby.
     * Basically just the object but without
     * an agent or team.
     *
     * @param name Full player name & tag.
     * @param level Player level.
     * @param playerCardId Player card id.
     * @param playerTitleId Player title id.
     * @param currentTier Player current rank.
     * @param peakTier Player peak rank.
     *
     * @return Created Player.
     */
    public static Player createLobbyPlayer(
            final String name,
            final int level,
            final String playerCardId,
            final String playerTitleId,
            final Tier currentTier,
            final Tier peakTier
    ) {
        return new Player(
                name, level, playerCardId, playerTitleId,
                null, null, null,
                currentTier, peakTier
        );
    }
}
