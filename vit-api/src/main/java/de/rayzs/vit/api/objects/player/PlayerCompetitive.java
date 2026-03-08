package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.competitive.MatchResult;
import de.rayzs.vit.api.objects.player.competitive.SeasonTiers;

public record PlayerCompetitive(
        Tier currentTier,
        int rr,
        SeasonTiers seasonTiers,
        MatchResult latestMatch,
        CompRequirements compRequirements
) { }
