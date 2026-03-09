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
import de.rayzs.vit.api.objects.player.PlayerStats;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.launch.processes.gui.screens.Screen;

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
        for (int i = 0; i < random.nextInt(15); i++) {
            matches.add(createRandomMatch());
        }

        int headshotHits = 0, shotHits = 0, wins = 0, games = 0;
        for (final Match playedMatch : matches) {
            final MatchInfo info = playedMatch.stats();

            headshotHits += info.headshots();
            shotHits += info.headshots() + info.bodyShots() + info.legShots();

            games++;

            if (info.won()) wins++;
        }

        final float headshotRate = (float) headshotHits / (float) shotHits;
        final float winRate = (float) wins / (float) games;

        final PlayerStats playerStats = new PlayerStats(winRate, headshotRate);

        return new Player(
                String.valueOf(Math.abs(random.nextLong())),
                team,
                generateRandomPlayerName(),
                Agent.values()[random.nextInt(Agent.values().length)],
                random.nextInt(500),
                "PlayerCard-" + random.nextInt(100),
                "PlayerTitle-" + random.nextInt(100),
                settings,
                inventory,
                null,
                playerStats,
                matches.toArray(new Match[0])
        );
    }

    private static Match createRandomMatch() {
        final Collection<String> maps = VIT.get().getImageProvider().getMaps().getIds();
        final String[] mapsArray = maps.toArray(new String[0]);

        final String mapId = mapsArray[random.nextInt(mapsArray.length)];

        final int wonRounds = random.nextInt(10),
                lostRounds = random.nextInt(10);

        return new Match(
                String.valueOf(Math.abs(random.nextLong())),
                mapId,
                new MatchInfo(
                        new Season("", "", "", false),
                        random.nextFloat(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        wonRounds,
                        lostRounds,
                        wonRounds >= lostRounds
                ),
                new CompMatchResult(
                        random.nextInt(33)
                )
        );
    }

    private static String generateRandomPlayerName() {
        return "Player-"
                + UUID.randomUUID().toString().substring(0, random.nextInt(12))
                + "#"
                + random.nextInt(10000);
    }
}
