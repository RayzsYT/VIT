package de.rayzs.vit.asl;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.event.EventAdapter;
import de.rayzs.vit.api.event.events.game.match.GameMatchStartEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchStartEvent;
import de.rayzs.vit.api.event.events.player.PreCreatePlayerBannerEvent;
import de.rayzs.vit.api.event.events.player.PreCreatePlayerWindowEvent;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.PlayerSettings;

public class AlwaysShowLevelsAddon extends Addon {

    public AlwaysShowLevelsAddon(final VITAPI api, final AddonDescription description) {
        super(api, description);
    }

    @Override
    public void onEnable() {

        api.getEventManager().register(this, new EventAdapter<>(GamePreMatchStartEvent.class) {

            @Override
            public void call(final GamePreMatchStartEvent event) {
                for (int i = 0; i < event.getGame().players().length; i++) {
                    final Player player = event.getGame().players()[i];
                    event.getGame().players()[i] = createModifiedPlayer(player);
                }
            }
        });

        api.getEventManager().register(this, new EventAdapter<>(GameMatchStartEvent.class) {

            @Override
            public void call(final GameMatchStartEvent event) {
                for (int i = 0; i < event.getGame().players().length; i++) {
                    final Player player = event.getGame().players()[i];
                    event.getGame().players()[i] = createModifiedPlayer(player);
                }
            }
        });

        api.getEventManager().register(this, new EventAdapter<>(PreCreatePlayerBannerEvent.class) {

            @Override
            public void call(final PreCreatePlayerBannerEvent event) {
                event.setPlayer(createModifiedPlayer(event.getPlayer()));
            }
        });

        api.getEventManager().register(this, new EventAdapter<>(PreCreatePlayerWindowEvent.class) {

            @Override
            public void call(final PreCreatePlayerWindowEvent event) {
                event.setPlayer(createModifiedPlayer(event.getPlayer()));
            }
        });
    }

    private Player createModifiedPlayer(final Player player) {
        final PlayerSettings settings = player.settings();

        return !settings.levelHidden() ? player : new Player(
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
