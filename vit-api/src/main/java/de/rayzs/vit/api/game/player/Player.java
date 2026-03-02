package de.rayzs.vit.api.game.player;

import de.rayzs.vit.api.game.items.Agent;

public record Player(
        String name,
        Agent agent,
        int level,
        String playerCardId,
        String playerTitleId
) { }
