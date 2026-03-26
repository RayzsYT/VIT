package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.MatchMap;

public record LastSeenDetails(
        int times,                  // How often seen this player.
        long lastSeenTime,          // Last time when seen this player.
        MatchMap map,               // Map name.
        Agent agent                 //
) { }
