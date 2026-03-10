package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.match.LastCompMatch;
import de.rayzs.vit.api.objects.player.season.SeasonTiers;

public record PlayerCompetitive(
        Tier currentTier,
        int rr,
        SeasonTiers seasonTiers,
        LastCompMatch latestMatch,
        CompRequirements compRequirements
) { }
