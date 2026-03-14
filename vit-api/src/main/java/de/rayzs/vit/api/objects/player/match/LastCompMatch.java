package de.rayzs.vit.api.objects.player.match;

import de.rayzs.vit.api.objects.items.MatchMap;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;

public record LastCompMatch(
        MatchMap map,                       // Played map
        CompMatchResult compMatchResult     // Comp match results
) { }