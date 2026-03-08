package de.rayzs.vit.api.objects.player.match;

import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;

public record Match(
        String matchId,                         // ID how the match.
        String mapId,                           // ID of map being played on.
        MatchInfo stats,                        // Match stats
        CompMatchResult compMatchResult         // Comp match results
) { }
