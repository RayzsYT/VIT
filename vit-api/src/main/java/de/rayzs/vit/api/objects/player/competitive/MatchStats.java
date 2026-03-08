package de.rayzs.vit.api.objects.player.competitive;

/**
 * Match statistics.
 * @param matchId ID of the match.
 * @param mapId ID of map played there.
 * @param headshots Headshots.
 */
public record MatchStats(
        int matchId,            // ID how the match.
        String mapId,           // ID of map being played on.
        int headshots           // How much RR lost/gained after this match.
) { }
