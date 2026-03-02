package de.rayzs.vit.api.items.game.player;

import de.rayzs.vit.api.items.Agent;

public record Player(
        String name,
        Agent agent,
        int level,
        String playerCardId,
        String playerTitleId
) { }
