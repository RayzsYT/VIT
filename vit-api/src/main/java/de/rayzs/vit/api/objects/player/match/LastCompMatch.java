package de.rayzs.vit.api.objects.player.match;

import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;

public record LastCompMatch(
        String mapId,                       // ID of map being played on.
        CompMatchResult compMatchResult     // Comp match results
) { }