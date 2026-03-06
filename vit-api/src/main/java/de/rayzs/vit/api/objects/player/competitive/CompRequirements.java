package de.rayzs.vit.api.objects.player.competitive;

public record CompRequirements(
        int requiredCompGames,  // How many comp games are required for the player to be ranked in.
        boolean rankedIn        // Is player already ranked in or not.
) { }
