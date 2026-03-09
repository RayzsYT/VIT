package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Season;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.PlayerInventory;
import de.rayzs.vit.api.objects.player.PlayerSettings;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;
import de.rayzs.vit.api.objects.session.SessionState;
import de.rayzs.vit.processes.gui.screens.Screen;

import java.util.*;

public class TestDummy {

    private static final Random random = new Random();


    public static void apply(final MainGUI gui, final Screen screen, final int playerNum) {

        final Collection<String> maps = VIT.get().getImageProvider().getMaps().getIds();
        final String[] mapsArray = maps.toArray(new String[0]);

        final String mapId = mapsArray[random.nextInt(mapsArray.length)];

        final List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerNum; i++) {
            players.add(createRandomPlayer(i % 2 == 0 ? Team.ATTACK : Team.DEFEND));
        }

        final Player self = players.getFirst();

        final Game game = new Game(
                self,
                SessionState.IN_LOBBY, // Does not matter anyway
                players.toArray(new Player[0]),
                mapId,
                "Wambooo" // Only legends will understand
        );


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

        final List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            matches.add(createRandomMatch());
        }

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
                null,
                matches.toArray(new Match[0])
        );
    }

    private static Match createRandomMatch() {
        final Collection<String> maps = VIT.get().getImageProvider().getMaps().getIds();
        final String[] mapsArray = maps.toArray(new String[0]);

        final String mapId = mapsArray[random.nextInt(mapsArray.length)];

        return new Match(
                String.valueOf(Math.abs(random.nextLong())),
                mapId,
                new MatchInfo(
                        new Season("", "", "", false),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextBoolean()
                ),
                new CompMatchResult(
                        random.nextInt(33)
                )
        );
    }
}
