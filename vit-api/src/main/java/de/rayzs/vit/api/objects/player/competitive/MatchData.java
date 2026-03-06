package de.rayzs.vit.api.objects.player.competitive;

public record MatchData(
        int rr,         // How much RR lost/gained after this match.
        String mapId    // Id of map being played on.
) { }
