package de.rayzs.vit.api.objects.game;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.*;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.match.LastCompMatch;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;
import de.rayzs.vit.api.objects.player.season.SeasonTiers;
import de.rayzs.vit.api.session.SessionState;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public record Game(
        Player self,            // Self player
        SessionState state,     // State
        Player[] players,       // Players
        String mapId,           // ID of map
        String server           // Connected server
) {


    /**
     * Stored the match into a file.
     *
     * @param game Match to store.
     *
     * @return True if match could be stored successfully. False otherwise.
     */
    public static boolean saveMatch(final Game game) {
        final File saveFile = FileDir.GAMES.getFile(VITAPI.DATE_FORMAT.format(System.currentTimeMillis()) + ".o");

        try {
            final FileOutputStream outputStream = new FileOutputStream(saveFile);
            final ObjectOutputStream write = new ObjectOutputStream(outputStream);

            final Compressor.CompressedGame compressedGame = new Compressor.CompressedGame(game);

            write.writeObject(compressedGame);
            write.close();

            return true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * Loads a stored match from a file.
     * If successful, it will return it as a {@link Game} object.
     * If it failed, it will return NULL instead.
     *
     * @param saveFile File to read the game match from.
     * @return {@link Game} is successful. NULL otherwise.
     */
    public static Game loadMatch(final File saveFile) {
        try {
            final FileInputStream inputStream = new FileInputStream(saveFile);
            final ObjectInputStream read = new ObjectInputStream(inputStream);

            if (read.readObject() instanceof Compressor.CompressedGame game) {
                read.close();


                final String mapId = game.getMapId();
                final String server = game.getServerId();

                final Compressor.CompressedPlayer[] compressedPlayers = game.getPlayers();
                final Player[] players = new Player[compressedPlayers.length];

                Player selfPlayer = null;

                for (int i = 0; i < compressedPlayers.length; i++) {
                    final Compressor.CompressedPlayer compressedPlayer = compressedPlayers[i];

                    final Map<Weapon, String> weaponSkins = new HashMap<>();
                    for (Map.Entry<String, String> entry : compressedPlayer.weaponSkins.entrySet()) {
                        final Weapon weapon = Weapon.getWeaponByName(entry.getKey());
                        final String skinId = entry.getValue();

                        weaponSkins.put(weapon, skinId);
                    }

                    final Compressor.CompressedMatch[] compressedMatches = compressedPlayer.matchHistory;
                    final Match[] matches = new Match[compressedMatches.length];

                    for (int j = 0; j < compressedMatches.length; j++) {
                        final Compressor.CompressedMatch match = compressedMatches[j];
                        matches[j] = new Match(
                                match.matchId,
                                match.mapId,
                                new MatchInfo(
                                        null,
                                            match.headshotRate,
                                        match.headShots,
                                        match.bodyShots,
                                        match.legShots,
                                        match.wonRounds(),
                                        match.lostRounds,
                                        match.won
                                ),
                                new CompMatchResult(
                                        match.earnedRR
                                )
                        );
                    }

                    players[i] = new Player(
                            compressedPlayer.playerId,
                            Team.getTeamByName(compressedPlayer.teamName),
                            compressedPlayer.playerName,
                            Agent.getAgentByName(compressedPlayer.agentName),
                            compressedPlayer.levels,
                            compressedPlayer.playerCardId,
                            compressedPlayer.playerTitleId,
                            new PlayerSettings(
                                    compressedPlayer.hideLevels,
                                    compressedPlayer.incognito
                            ),
                            new PlayerInventory(
                                    weaponSkins
                            ),
                            new PlayerCompetitive(
                                    Tier.getTierById(compressedPlayer.currentTierId),
                                    compressedPlayer.currentRR,
                                    compressedPlayer.seasonTiers,
                                    new LastCompMatch(
                                            compressedPlayer.lastCompMatchMapName,
                                            new CompMatchResult(
                                                    compressedPlayer.lastEarnedRR
                                            )
                                    ),
                                    new CompRequirements(
                                            compressedPlayer.compRequirementRounds,
                                            compressedPlayer.compRequirementRankedIn
                                    )
                            ),
                            new PlayerStats(
                                    compressedPlayer.winRate(),
                                    compressedPlayer.headshotRate
                            ),
                            matches
                    );

                    if (compressedPlayer.playerId.equalsIgnoreCase(game.self.playerId)) {
                        selfPlayer = players[i];
                    }
                }


                return new Game(
                        selfPlayer,
                        SessionState.IN_GAME, // Since it's only used to store matches and nothing more.
                        players,
                        mapId,
                        server
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }



    /**
     * A compressed version of the {@link Game} object.
     * Not very beautiful to watch, but it just has a
     * functional purpose for a one-time use after a match.
     * It's not meant to be seen by others anyway, so please
     * ignore this mess. ^^
     * **/
    private static class Compressor {

        private Compressor() {}


        private static class CompressedGame implements Serializable {
            private final String mapId, serverId;
            private final CompressedPlayer[] players;
            private CompressedPlayer self;

            public CompressedGame(final Game game) {

                this.mapId = game.mapId;
                this.serverId = game.server;
                this.players = new CompressedPlayer[game.players.length];

                for (int i = 0; i < game.players.length; i++) {
                    final Player player = game.players[i];
                    final CompressedMatch[] compressedMatches = new CompressedMatch[player.playedMatches().length];

                    for (int m = 0; m < compressedMatches.length; m++) {
                        Match match = player.playedMatches()[m];

                        compressedMatches[m] = new CompressedMatch(
                                match.matchId(),
                                match.mapId(),
                                match.stats().headshotRate(),
                                match.stats().headshots(),
                                match.stats().bodyShots(),
                                match.stats().legShots(),
                                match.stats().wonRounds(),
                                match.stats().lostRounds(),
                                match.stats().won(),
                                match.compMatchResult().rr()
                        );
                    }

                    final CompressedPlayer compressedPlayer = new CompressedPlayer(
                            player.id(),
                            player.settings().incognito(),
                            player.settings().levelHidden(),
                            player.level(),
                            null,
                            player.playerCardId(),
                            player.playerTitleId(),
                            player.name(),
                            player.agent().getAgentName(),
                            player.team().getTeamName(),
                            (player.competitive() != null ? player.competitive().currentTier() : Tier.UNRANKED).getTierId(),
                            (player.competitive() != null && player.competitive().seasonTiers() != null
                                    ? player.competitive().seasonTiers().getPeakTier()
                                    : Tier.UNRANKED).getTierId(),
                            player.stats().headShotRate(),
                            player.stats().winRate(),
                            player.competitive() != null ? player.competitive().latestMatch().compMatchResult().rr() : 0,
                            player.competitive() != null ? player.competitive().latestMatch().mapId() : null,
                            player.competitive() != null ? player.competitive().rr() : 0,
                            player.competitive() != null ? player.competitive().compRequirements().requiredCompGames() : 5,
                            player.competitive() != null && player.competitive().compRequirements().rankedIn(),
                            player.competitive() != null ? player.competitive().seasonTiers() : null,
                            compressedMatches
                    );

                    this.players[i] = compressedPlayer;

                    if (compressedPlayer.playerId.equalsIgnoreCase(game.self.id())) {
                        this.self = compressedPlayer;
                    }
                }

            }

            public CompressedPlayer getSelf() {
                return this.self;
            }

            public String getMapId() {
                return this.mapId;
            }

            public String getServerId() {
                return this.serverId;
            }

            public CompressedPlayer[] getPlayers() {
                return this.players;
            }
        }

        private record CompressedPlayer(
                String playerId,
                boolean incognito,
                boolean hideLevels,
                int levels,
                Map<String, String> weaponSkins, // weapon name, skin id
                String playerCardId,
                String playerTitleId,
                String playerName,
                String agentName,
                String teamName,
                String currentTierId,
                String peakTierId,
                float headshotRate,
                float winRate,
                int lastEarnedRR,
                String lastCompMatchMapName,
                int currentRR,
                int compRequirementRounds,
                boolean compRequirementRankedIn,
                SeasonTiers seasonTiers,
                CompressedMatch[] matchHistory

        ) implements Serializable { }

        private record CompressedMatch(
                String matchId,
                String mapId,
                float headshotRate,
                int headShots,
                int bodyShots,
                int legShots,
                int wonRounds,
                int lostRounds,
                boolean won,
                int earnedRR
        ) implements Serializable { }
    }
}
