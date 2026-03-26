package de.rayzs.vit.api.objects.player;

public record LastSeenDetails(
        int times,                  // How often seen this player.
        long lastSeenTime,          // Last time when seen this player.
        String lastSeenMatchId      // Last seen in match of said id.
) { }
