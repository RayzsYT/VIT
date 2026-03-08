package de.rayzs.vit.api.objects.game;

import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.session.SessionState;

public record Game(
        Player self,            // Self player
        SessionState state,     // State
        Player[] players,       // Players
        String mapId,           // ID of map
        String server           // Connected server
) { }
