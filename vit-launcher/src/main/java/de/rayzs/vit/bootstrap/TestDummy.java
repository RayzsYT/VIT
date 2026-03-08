package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.game.GameState;
import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.PlayerInventory;
import de.rayzs.vit.api.objects.player.PlayerSettings;
import de.rayzs.vit.processes.screen.Screen;

import java.util.*;

public class TestDummy {

    private static final Random random = new Random();
    private static Game game;

    static {

        final Collection<String> maps = VIT.get().getImageProvider().getMaps().getIds();
        final String[] mapsArray = maps.toArray(new String[0]);

        final String mapId = mapsArray[random.nextInt(mapsArray.length)];

        final List<Player> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            players.add(createRandomPlayer(i % 2 == 0 ? Team.ATTACK : Team.DEFEND));
        }

        final Player self = players.getFirst();

        game = new Game(
                self,
                GameState.LOBBY,
                players.toArray(new Player[0]),
                mapId,
                "Wambooo" // Only legends will understand
        );

    }

    public static void apply(final MainGUI gui, final Screen screen) {
        VIT.get().setGame(game);

        screen.load(VIT.get(), gui);
    }


    private static Player createRandomPlayer(final Team team) {

        final Collection<String> skins = VIT.get().getImageProvider().getWeaponSkins().getIds();
        final String[] skinsArr = skins.toArray(new String[0]);
        final Map<Weapon, String> skinMap = new HashMap<>();

        for (final Weapon weapon : Weapon.values()) {
            skinMap.put(weapon, skinsArr[random.nextInt(skinsArr.length)]);
        }

        final PlayerSettings settings = new PlayerSettings(false, false);
        final PlayerInventory inventory = new PlayerInventory(skinMap);

        return new Player(
                String.valueOf(Math.abs(random.nextLong())),
                team,
                "Player-" + random.nextInt(100),
                Agent.values()[random.nextInt(Agent.values().length)],
                random.nextInt(500),
                "PlayerCard-" + random.nextInt(100),
                "PlayerTitle-" + random.nextInt(100),
                settings,
                inventory,
                null
        );
    }
}
