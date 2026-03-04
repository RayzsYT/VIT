package de.rayzs.vit.api.game.player;

import de.rayzs.vit.api.game.items.Agent;
import de.rayzs.vit.api.game.items.Team;

public record Player(
        String name,
        Team team,
        Agent agent,
        int level,
        String peakTierId,
        String currentTierId,
        String playerCardId,
        String playerTitleId
) { }
