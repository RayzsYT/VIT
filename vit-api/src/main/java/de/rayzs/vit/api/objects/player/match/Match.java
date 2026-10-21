package de.rayzs.vit.api.objects.player.match;

import de.rayzs.vit.api.objects.items.MatchMap;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;

public record Match(
        String matchId,                         // ID how the match
        MatchMap map,                           // Played map
        MatchInfo stats,                        // Match stats
        CompMatchResult compMatchResult,        // Comp match results
        Player[] players                        // Players inside match (Only Team and Id)
) { }
