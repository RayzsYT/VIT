package de.rayzs.vit.api.objects.player.party;

import de.rayzs.vit.api.objects.player.Player;

import java.awt.*;

public record Party(
    int partyId,
    Color partyColor,
    Player[] members
) { }
