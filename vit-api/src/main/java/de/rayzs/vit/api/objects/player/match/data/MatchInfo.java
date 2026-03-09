package de.rayzs.vit.api.objects.player.match.data;

import de.rayzs.vit.api.objects.items.Season;

public record MatchInfo(
        Season season,
        int headshots,
        int bodyShots,
        int legShots,
        int wonRounds,
        int lostRounds,
        boolean won
) { }
