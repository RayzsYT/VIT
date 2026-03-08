package de.rayzs.vit.api.objects.player.match.data;

/**
 * Match statistics.
 *
 * @param headshotRate Headshot rate.
 */
public record MatchInfo(
        float headshotRate           // How much RR lost/gained after this match.
) { }
