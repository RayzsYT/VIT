package de.rayzs.vit.asl;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.event.EventAdapter;
import de.rayzs.vit.api.event.events.game.match.GameMatchStartEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchStartEvent;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.PlayerSettings;

public class AlwaysShowLevelsAddon extends Addon {

    public AlwaysShowLevelsAddon(VITAPI api, AddonDescription description) {
        super(api, description);
    }

    @Override
    public void onEnable() {

        api.getEventManager().register(this, new EventAdapter<>(GamePreMatchStartEvent.class) {

            @Override
            public void call(GamePreMatchStartEvent event) {
                for (int i = 0; i < event.getGame().players().length; i++) {
                    final Player player = event.getGame().players()[i];
                    event.getGame().players()[i] = createModifiedPlayer(player);
                }
            }
        });

        api.getEventManager().register(this, new EventAdapter<>(GameMatchStartEvent.class) {

            @Override
            public void call(GameMatchStartEvent event) {
                for (int i = 0; i < event.getGame().players().length; i++) {
                    final Player player = event.getGame().players()[i];
                    event.getGame().players()[i] = createModifiedPlayer(player);
                }
            }
        });
    }

    private Player createModifiedPlayer(final Player player) {
        final PlayerSettings settings = player.settings();

        return new Player(
                player.id(),
                player.team(),
                player.name(),
                player.agent(),
                player.level(),
                player.playerCardId(),
                player.playerTitleId(),
                new PlayerSettings(
                        false,
                        settings.incognito()
                ),
                player.inventory(),
                player.competitive(),
                player.stats(),
                player.party(),
                player.lastSeenDetails(),
                player.playedMatches()
        );
    }
}
