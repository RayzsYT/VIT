package de.rayzs.vit.api.objects.player.season;

import de.rayzs.vit.api.objects.items.Season;

import java.io.Serializable;

public record SeasonStats(
        Season season,      // Season
        int rr,             // How much RR in this season
        int playedGames,    // Total played comp games.
        float winRate,      // Win-rate based on won and played comp games.
        int wonGames,       // Total won comp games.
        int lostGames       // Total lost comp games.
) implements Serializable { }
