package de.rayzs.vit.api.objects.player.match.data;

/**
 * Comp match results.
 *
 * @param rr Lost/Gained RR.
 */
public record CompMatchResult(
        int rr                  // How much RR lost/gained after this match.
) { }
