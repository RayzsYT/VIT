package de.rayzs.vit.api.objects.player.competitive;

public record CompMatchResult(
        int rr,         // How much RR lost/gained after this match.
        String mapId    // Id of map being played on.
) { }
