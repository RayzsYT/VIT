package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.party.Party;

public record Player(
        String id,                          // Player id
        Team team,                          // Player team
        String name,                        // Player full name
        Agent agent,                        // Player agent
        int level,                          // Player level
        String playerCardId,                // Player card id
        String playerTitleId,               // Player title id
        PlayerSettings settings,            // Player settings,
        PlayerInventory inventory,          // Skin inventory
        PlayerCompetitive competitive,      // Stored competitive information
        PlayerStats stats,                  // Player stats
        Party party,                        // Party the player belongs to
        Match[] playedMatches               // Played matches
) { }
