package de.rayzs.vit.api.objects.player.competitive;

/**
 * What the player gained from that match.
 *
 * @param mapId ID of map played there.
 * @param rr Gained RR.
 */
public record MatchResult(
        String mapId,           // ID of map being played on.
        int rr                  // How much RR lost/gained after this match.
) { }
