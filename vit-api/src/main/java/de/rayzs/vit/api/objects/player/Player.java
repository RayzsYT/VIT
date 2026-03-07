package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;

public record Player(
        String id,                      // Player id
        Team team,                      // Player team
        String name,                    // Player full name
        Agent agent,                    // Player agent
        int level,                      // Player level
        String playerCardId,            // Player card id
        String playerTitleId,           // Player title id
        PlayerSettings settings,        // Player settings,
        PlayerInventory inventory,      // Player inventory
        PlayerCompetitive competitive   // Player stored competitive information
) { }
