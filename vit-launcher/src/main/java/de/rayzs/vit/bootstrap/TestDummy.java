package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.launch.guis.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.*;
import de.rayzs.vit.api.objects.player.*;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.match.LastCompMatch;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.launch.screens.ScreenAbstr;

import java.util.*;
import java.util.Map;

public class TestDummy {

    private static final Random random = new Random();


    public static void apply(
            final MainGUI gui,
            final ScreenAbstr screen,
            final int playerNum,
            final boolean saveMatch
    ) {

        if (gui != null) {
            gui.setVisible(true);
        }


        final MatchMap map = getRandomMap();

        final List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerNum; i++) {
            players.add(createRandomPlayer(i % 2 == 0 ? Team.ATTACK : Team.DEFEND));
            //players.add(createRandomPlayer(Team.DEFEND));
        }

        final Player self = players.getFirst();

        final Game game = new Game(
                self,
                SessionState.IN_LOBBY, // Does not matter anyway
                players.toArray(new Player[0]),
                null,
                map,
                "Wambooo" // Only legends will understand
        );


        VIT.get().setGame(game);

        if (gui != null) {
            screen.load(VIT.get(), gui);
        }


        if (saveMatch) {
            Game.saveMatch(game);
        }
    }

    static int i = 0;


    private static Player createRandomPlayer(final Team team) {

        final Collection<String> skins = VIT.get().getImageProvider().getWeaponSkins().getIds();
        final String[] skinsArr = skins.toArray(new String[0]);
        final Map<Weapon, String> skinMap = new HashMap<>();

        for (int i = 0; i < Weapon.values().length; i++) {
            final Weapon weapon = Weapon.values()[i];
            final String skinId = skinsArr[random.nextInt(skinsArr.length)];
            final String skinName = VIT.get().getImageProvider().getWeaponSkins().getName(skinId);

            if (skinName.contains(weapon.getWeaponName())) {
                skinMap.put(weapon, skinId);
                continue;
            }

            i--;
        }

        final PlayerSettings settings = new PlayerSettings(
                random.nextInt(10) > 4,
                random.nextInt(10) > 4
        );

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
        final MatchMap map = getRandomMap();

        final Agent agent = Agent.values()[random.nextInt(Agent.values().length)];

        return new Player(
                String.valueOf(Math.abs(random.nextLong())),
                team,
                generateRandomPlayerName(),
                agent,
                random.nextInt(500),
                "PlayerCard-" + random.nextInt(100),
                "PlayerTitle-" + random.nextInt(100),
                settings,
                inventory,
                new PlayerCompetitive(
                        Tier.values()[random.nextInt(Tier.values().length)],
                        random.nextInt(100),
                        null,
                        new LastCompMatch(
                                map,
                                new CompMatchResult(
                                        (random.nextInt(10) <= 4 ? -1 : 1) * random.nextInt(30)
                                )
                        ),
                        new CompRequirements(
                                random.nextInt(10),
                                true
                        )
                ),
                playerStats,
                null,
                random.nextInt(10) < 7 ? null : new LastSeenDetails(
                        1,
                        System.currentTimeMillis() - (10000 * random.nextInt(60) * random.nextInt(10)),
                        map,
                        agent
                ),
                matches.toArray(new Match[0])
        );
    }

    private static Match createRandomMatch() {
        final MatchMap map = getRandomMap();

        final int wonRounds = random.nextInt(10),
                lostRounds = random.nextInt(10);

        return new Match(
                String.valueOf(Math.abs(random.nextLong())),
                map,
                new MatchInfo(
                        new Season("", "", SeasonType.ACT, null),
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
                + UUID.randomUUID().toString().substring(0, random.nextInt(6))
                + "#"
                + random.nextInt(10000);
    }

    private static MatchMap getRandomMap() {
        final Collection<MatchMap> maps = MatchMap.getMaps();
        final MatchMap[] mapsArray = maps.toArray(new MatchMap[0]);

        return mapsArray[random.nextInt(mapsArray.length)];
    }
}
