package de.rayzs.vit.launch;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.database.Database;
import de.rayzs.vit.api.database.DatabaseHandler;
import de.rayzs.vit.api.event.events.game.GameInitializedEvent;
import de.rayzs.vit.api.event.events.game.PreGameInitializeEvent;
import de.rayzs.vit.api.event.events.player.PreFetchPlayerNameEvent;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.*;
import de.rayzs.vit.api.objects.player.*;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.party.Party;
import de.rayzs.vit.api.objects.player.party.PartyColors;
import de.rayzs.vit.api.objects.player.season.SeasonStats;
import de.rayzs.vit.api.objects.player.season.SeasonTiers;
import de.rayzs.vit.api.objects.player.match.LastCompMatch;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.match.data.CompMatchResult;
import de.rayzs.vit.api.objects.player.match.data.MatchInfo;
import de.rayzs.vit.api.request.Requests;
import de.rayzs.vit.api.session.Session;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import de.rayzs.vit.api.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map;
import java.util.function.Consumer;

public class ImplSession implements Session {


    private final File lockfile;

    private String currentVersion;

    private HttpClient client;
    private String selfPlayerId;

    public ImplSession() {
        this.lockfile = FileDir.VALORANT_CONF.getFile("lockfile");
        this.client = Request.createClient();

        fetchAuthToken();
        fetchRequestUrls();
    }


    /**
     * Initializes all required information
     * to send requests. Should not be called
     * during runtime once it already has been called,
     * unless VALORANT has been restarted.
     * <p>
     * It unsets all request important information
     * and updates them, which is a pretty heavy process.
     * So please only call it if necessary, in case
     * VALORANT isn't booted and needs to be detected.
     */
    @Override
    public void initialize() {
        this.client = Request.createClient();

        if (Request.isAuthTokenSet()) {
            Request.unsetAuthToken();
            System.out.println("Unset auth token to fetch it again.");
        }

        if (Request.areHeadersSet()) {
            Request.unsetHeaders();
            System.out.println("Unset headers to fetch it again.");
        }

        fetchAuthToken();
        fetchRequestHeaders(this.client);
    }


    /**
     * Get the client to send requests
     * to the VALORANT servers.
     *
     * @return HttpClient.
     */
    @Override
    public HttpClient getClient() {
        return this.client;
    }



    /**
     * Fetch and set the auth token.
     */
    private void fetchAuthToken() {
        System.out.println("Fetching auth token...");

        try {
            String[] lockData = new String(Files.readAllBytes(lockfile.toPath())).split(":");

            final String port = lockData[2];
            final String password = lockData[3];

            final String authToken = Base64.getEncoder().encodeToString(
                    ("riot:" + password).getBytes(StandardCharsets.UTF_8)
            );

            RequestDest.LOCAL.update(port);
            Request.setAuthToken(authToken);

        } catch (IOException exception) {
            throw new RuntimeException("Failed to set access token! Failed to read the information inside the lock data file.", exception);
        }
    }

    /**
     * Fetches all the necessary information
     * to send the requests to the correct servers
     * by reading and applying the correct regions
     * and ports for creating local connections.
     */
    private void fetchRequestUrls(){
        String firstRegion = null;
        String secondRegion = null;
        String currentVersion = null;

        try {
            final File logFile = FileDir.VALORANT_LOGS.getFile("ShooterGame.log");

            for (String line : Files.readAllLines(logFile.toPath())) {
                if (currentVersion != null && firstRegion != null && secondRegion != null) {
                    break;
                }

                String[] parts;


                // Set 'CURRENT_VERSION':
                if (currentVersion == null) {
                    final int versionIndex = StringUtils.searchIndex("CI server version: ", line);

                    if (versionIndex != -1) {
                        parts = line.substring(versionIndex).split("-");

                        final List<String> partsList = new ArrayList<>(Arrays.asList(parts));
                        if (partsList.size() >= 2 && !partsList.get(2).equalsIgnoreCase("shipping")) {
                            partsList.add(2, "shipping");
                        }

                        currentVersion = String.join("-", partsList);
                        continue;
                    }
                }

                // Set first region for requests.
                if (firstRegion == null) {
                    final int pdIndex = StringUtils.searchIndex("https://pd.", line);
                    final int sharedIndex = StringUtils.searchIndex("https://shared.", line);

                    if (pdIndex != -1) {
                        parts = line.substring(pdIndex).split("\\.", 2);
                        firstRegion = parts[0];
                        continue;
                    }

                    if (sharedIndex != -1) {
                        parts = line.substring(sharedIndex).split("\\.", 2);
                        firstRegion = parts[0];
                        continue;
                    }
                }

                // Set first/second region for requests.
                if (secondRegion == null) {
                    final int glzIndex = StringUtils.searchIndex("https://glz-", line);

                    if (glzIndex != -1) {
                        parts = line.substring(glzIndex).split("\\.", 2);
                        secondRegion = parts[0];
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Failed to fetch the regions!", exception);
        }


        // Safe-keeping checks just to ensure that everything was actually successful.
        // I had my share of experience with programs that stop working
        // just partially and still keep running. (I NEED TO KNOW :D)
        if (firstRegion == null) {
            throw new NullPointerException("Failed to fetch the first region!");
        }

        if (secondRegion == null) {
            throw new NullPointerException("Failed to fetch the second region!");
        }

        if (currentVersion == null) {
            throw new NullPointerException("Failed to fetch the current VALORANT client version!");
        }


        this.currentVersion = currentVersion;


        RequestDest.SHARED.update(firstRegion);
        RequestDest.PD.update(firstRegion);

        RequestDest.GLZ.update(secondRegion, firstRegion);
    }


    /**
     * Fetches and sets the headers for creating requests
     * to the VALORANT servers.
     *
     * @param client HttpClient sending the request.
     */
    private void fetchRequestHeaders(final HttpClient client) {
        System.out.println("Fetching headers...");

        // Sending request to receive the required headers
        // for sending requests towards the VALORANT servers.
        final JSONObject json = Requests.Get.General.fetchToken(client);

        if (json == null) {
            throw new NullPointerException("Request failed!");
        }

        if (this.currentVersion == null) {
            throw new NullPointerException("Current version is not set yet! Please only call this method here once 'fetchRequestUrls' has been called first.");
        }


        final String accessToken = json.getString("accessToken");
        final String entitlementToken = json.getString("token");

        // Own player's id.
        this.selfPlayerId = json.getString("subject");


        // 'currentVersion' is loaded in advance inside the 'fetchRequestUrls'
        // since in there the same file, which already includes the client version,
        // is there as well. I mean, better than doing things twice a row, right?
        // Also, I doubt the version is gonna change while the tracker is live.
        Request.setHeaders(accessToken, entitlementToken, this.currentVersion);
    }


    /**
     * Get the session state by sending a
     * request.
     * <p>
     * Returns a SessionState indicating whether
     * the client is currently in the menu, in a match,
     * of if VALORANT is even started.
     *
     * @return SessionState.
     */
    @Override
    public SessionState getSessionState() {


        if (this.selfPlayerId == null) {
            throw new NullPointerException("Self player id is not set yet! Please ensure to only call this method until 'fetchRequestHeaders' has been called first.");
        }


        final JSONObject presence = Requests.Get.General.fetchPresence(client);

        if (presence == null) {
            return SessionState.VALORANT_NOT_OPEN;
        }


        final JSONArray presences = presence.getJSONArray("presences");

        for (Object presenceObj : presences) {
            final JSONObject currentPresence = (JSONObject) presenceObj;

            final String product = currentPresence.getString("product");
            final String playerId = currentPresence.getString("puuid");

            if (!product.equalsIgnoreCase("valorant") || !playerId.equalsIgnoreCase(this.selfPlayerId)) {
                continue;
            }


            // Sometimes exactly when VALORANT boots and the tracker
            // is enabled, the private key isn't fully build yet.
            // So to avoid any confusions or any issues, we'll simply ignore
            // it in that exact time and wait for the next iteration.
            final Object encodedPrivateObj = currentPresence.get("private");
            if (! (encodedPrivateObj instanceof String encodedPrivate)) {
                continue;
            }

            final String decodedPrivate = new String(Base64.getDecoder().decode(encodedPrivate), StandardCharsets.UTF_8);

            final JSONObject presenceData = new JSONObject(decodedPrivate);
            final JSONObject matchPresenceData = presenceData.getJSONObject("matchPresenceData");
            final String sessionLoopState = matchPresenceData.getString("sessionLoopState");

            return SessionState.from(sessionLoopState);
        }

        return SessionState.VALORANT_NOT_OPEN;
    }



    /**
     * Construct the game object which loads
     * all players and the map being played on.
     *
     * @param state Current session state.
     * @param preGameConsumer Consumer before game object is actually built.
     * @param playerLoadConsumer A consumer with the amount of currently loaded players.
     *
     * @return Constructed {@link Game} object.
     */
    @Override
    public Game constructGame(
            final SessionState state,
            final Consumer<PreGameInitializeEvent> preGameConsumer,
            final Consumer<Integer> playerLoadConsumer
    ) {

        updateSeasonActivity();     // Update all season activities.

        final List<String> incognitoPlayerIds = new ArrayList<>();    // Players in incognito
        final List<Player> registeredPlayers = new ArrayList<>();     // Registered Players

        final int loadPlayerMatchesCount = VIT.get().getSettings().get().optInt("load-player-matches-count", 5);

        PlayerCompetitive playerCompetitive = null;

        if (!state.isInsideMatch()) {
            throw new IllegalStateException("Session state makes no sense if you want to fetch your lobby data... (" + state.name() + ")");
        }


        final JSONObject initialMatchJson = Requests.Get.Match.fetchMatchData(client, state, selfPlayerId);

        if (initialMatchJson == null) {
            throw new IllegalStateException("Failed to fetch match data!");
        }


        final String matchId = initialMatchJson.getString("MatchID");


        // Fetch match information
        final JSONObject match = Requests.Get.Match.fetchLiveMatchDetails(client, state, matchId);

        if (match == null) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch match details.");
        }


        String tmpServer = match.getString("GamePodID");
        tmpServer = tmpServer.substring(0, tmpServer.lastIndexOf("-"));
        tmpServer = tmpServer.substring(tmpServer.lastIndexOf("-") + 1);
        tmpServer = Character.toUpperCase(tmpServer.charAt(0)) + tmpServer.substring(1);


        final PreGameInitializeEvent preGameInitializeEvent = VIT.get().getEventManager().call(new PreGameInitializeEvent(
                state, tmpServer, MatchMap.getMapByUrl(match.getString("MapID"))
        ));

        final MatchMap map = preGameInitializeEvent.getMap();

        final String mapId = map.mapId();
        final String server = preGameInitializeEvent.getServer();


        preGameConsumer.accept(preGameInitializeEvent);


        // Fetch the loadouts of all players.

        final JSONArray loadouts = Requests.Get.Player.fetchPlayerLayouts(client, state, matchId);
        if (loadouts == null) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch match loadouts.");
        }

        // Player id, Skin inventory
        final Map<String, PlayerInventory> playerInventories = state == SessionState.IN_GAME
                ? fetchInventory(loadouts)
                : Map.of();


        // Get list of all players.
        final JSONArray players = (state == SessionState.IN_LOBBY
                // Since it's the lobby, enemy team won't be shared yet.
                ? match.getJSONArray("Teams").getJSONObject(0)
                : match
        ).getJSONArray("Players");


        Team ownTeam = null;

        if (state == SessionState.IN_LOBBY) {
            ownTeam = Team.getTeamById(match
                    .getJSONArray("Teams")
                    .getJSONObject(0)
                    .getString("TeamID")
            );
        }


        // Preparing a json of players whose names we want
        // to ask for. We also use the opportunity to check
        // for each and every user's party id.

        final Map<String, Set<String>> playerParties = new HashMap<>(); // Party id, Set of player ids
        final JSONArray playersArray = new JSONArray();

        for (Object playerObj : players) {

            final JSONObject player = (JSONObject) playerObj;
            final String playerId = player.getString("Subject");


            final JSONObject party = null; // Requests.Get.Player.fetchPlayerParty(client, playerId);
            if (party != null) {
                final String partyId = party.getString("CurrentPartyID");

                playerParties.computeIfAbsent(
                        partyId,
                        k -> new HashSet<>()
                ).add(playerId);
            }

            /*
            In order to be compliant and not expose any streamers,
            incognito users won't be checked. Otherwise, the true name
            of those players will be exposed, which might lead to a ban.
            */

            final JSONObject identity = player.getJSONObject("PlayerIdentity");
            final boolean incognito = identity.getBoolean("Incognito");

            final PreFetchPlayerNameEvent preFetchPlayerNameEvent = new PreFetchPlayerNameEvent(playerId, incognito);

            if (!preFetchPlayerNameEvent.isIncognito()) {
                playersArray.put(playerId);
                continue;
            }

            incognitoPlayerIds.add(playerId);
        }


        // Now filtering for any parties that have less than 2 members.
        playerParties.entrySet().removeIf(entry -> entry.getValue().size() <= 1);

        final Map<String, Party> parties = new HashMap<>(); // Party id, Party

        { // Never done that before, but I want to do this in a separate scope. :]
            int partyIndex = 0;
            for (Map.Entry<String, Set<String>> entry : playerParties.entrySet()) {
                final String partyId = entry.getKey();

                final Party party = new Party(
                        partyId,
                        PartyColors.getPartyColor(partyIndex++),
                        new Player[20] // Might sound weird, but just in case someone has a weird and huuuge custom game. xd
                );

                parties.put(partyId, party);
            }
        }


        // Asking VALORANT for the information of the players inside
        // our match.
        final JSONArray playerNames = Requests.Send.Player.sendPlayerNameRequest(client, playersArray);
        if (playerNames == null) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch player names.");
        }


        final Map<String, String> playerNamesMap = new HashMap<>(); // Player ID, Name + Tag

        for (int i = 0; i < playerNames.length(); i++) {
            final JSONObject player = playerNames.getJSONObject(i);

            final String playerId = player.getString("Subject");
            final String playerName = player.getString("GameName") + "#" + player.getString("TagLine");


            playerNamesMap.put(playerId, playerName);
        }


        for (int i = 0; i < players.length(); i++) {
            final JSONObject playerJson = players.getJSONObject(i);

            final String playerId = playerJson.getString("Subject");
            final String playerName = playerNamesMap.get(playerId);


            // Competitive information about the player...
            final JSONObject rank = Requests.Get.Player.fetchPlayersMMR(client, playerId);

            if (rank != null) {
                final Object latestCompGameObj = rank.get("LatestCompetitiveUpdate");

                final JSONObject competitive = rank.getJSONObject("QueueSkills").getJSONObject("competitive");
                final int requiredRankGames = competitive.getInt("TotalGamesNeededForRating");
                final boolean rankedIn = requiredRankGames == 0;

                LastCompMatch lastMatch = null;
                if (latestCompGameObj instanceof JSONObject latestCompGame) {
                    final String lastPlayedMapUrl = latestCompGame.getString("MapID");
                    final int lastReceivedRR = latestCompGame.getInt("RankedRatingEarned");

                    lastMatch = new LastCompMatch(
                            MatchMap.getMapByUrl(lastPlayedMapUrl),
                            new CompMatchResult(lastReceivedRR)
                    );
                }


                final CompRequirements compRequirements = new CompRequirements(
                        requiredRankGames,
                        rankedIn
                );

                playerCompetitive = constructPlayerCompetitive(
                        lastMatch,
                        compRequirements,
                        competitive
                );

            } else {
                System.err.println("Failed to fetch competitive information of player: " + playerName + ". ( " + playerId + " )");
            }



            // Only fetches a certain amount of matches of the player. Should be enough. By default, it's set to 5 matches.
            final JSONObject matchHistory = Requests.Get.Match.fetchMatchHistory(client, playerId, 0, loadPlayerMatchesCount);
            final List<Match> playedMatchesList = new ArrayList<>(); // Match history

            if (matchHistory != null) {

                if (matchHistory.has("Matches")) {
                    final JSONArray playedMatches = matchHistory.getJSONArray("Matches");

                    for (final Object playedMatchObj : playedMatches) {
                        wait(PER_MATCH_COOLDOWN);



                        final JSONObject playedMatch = (JSONObject) playedMatchObj;


                        final String seasonId = playedMatch.getString("SeasonID");
                        if (!Season.isActive(Season.getSeasonById(seasonId))) {
                            continue;
                        }


                        final String playedMatchId = playedMatch.getString("MatchID");
                        final int gainedRR = playedMatch.has("RankedRatingEarned")
                                ? playedMatch.getInt("RankedRatingEarned")
                                : 0;


                        // Now trying to get the match information
                        final JSONObject playedMatchDetails = Requests.Get.Match.fetchPastMatchDetails(client, playedMatchId);


                        // Simply cancel the process to fetch the  match history of that player.
                        // Most of the time, when the first match-id failed, then the others fail as well.
                        // So better not asking for the others.
                        if (playedMatchDetails == null) {
                            System.err.println("Failed to fetch match details for " + playerName + "! Ignoring match history of that player entirely to prevent spamming the VALORANT API any further.");
                            break;
                        }


                        // Construct match stats to add in the list of match history.
                        final Match historyMatch = constructMatch(
                                playerId,
                                gainedRR,
                                playedMatchId,
                                playedMatchDetails
                        );

                        playedMatchesList.add(historyMatch);
                    }
                }
            }


            final JSONObject identity = playerJson.getJSONObject("PlayerIdentity");

            final boolean incognito = incognitoPlayerIds.contains(playerId);
            final boolean levelHidden = identity.getBoolean("HideAccountLevel");
            final int level = identity.getInt("AccountLevel");

            final String playerCardId = identity.getString("PlayerCardID");
            final String playerTitleId = identity.getString("PlayerTitleID");

            final Team team = state == SessionState.IN_LOBBY
                    ? ownTeam
                    : Team.getTeamById(playerJson.getString("TeamID"));

            final PlayerSettings settings = new PlayerSettings(
                    levelHidden,
                    incognito
            );

            final PlayerInventory inventory = playerInventories.get(playerId);


            final String agentId = state == SessionState.IN_GAME
                    ? playerJson.getString("CharacterID")
                    : null;

            final Agent agent =  state == SessionState.IN_GAME
                    ? Agent.getAgentById(agentId)
                    : null;


            int headshotHits = 0, shotHits = 0, wins = 0, games = 0;
            for (final Match playedMatch : playedMatchesList) {
                final MatchInfo info = playedMatch.stats();

                headshotHits += info.headshots();
                shotHits += info.headshots() + info.bodyShots() + info.legShots();

                games++;

                if (info.won()) wins++;
            }

            final float headshotRate = (float) headshotHits / (float) shotHits;
            final float winRate = (float) wins / (float) games;


            registeredPlayers.add(new Player(
                    playerId,
                    team,
                    Objects.requireNonNullElse(playerName,
                            agent == null ? "Hidden" : agent.getAgentName()
                    ),
                    agent,
                    level,
                    playerCardId,
                    playerTitleId,
                    settings,
                    inventory,
                    playerCompetitive,
                    new PlayerStats(winRate, headshotRate),
                    parties.get(playerId),
                    playedMatchesList.toArray(new Match[0])
            ));

            playerLoadConsumer.accept(registeredPlayers.size());


            wait(PER_PLAYER_COOLDOWN);
        }


        Player selfPlayer = null;
        for (final Player registeredPlayer : registeredPlayers) {
            if (registeredPlayer.id().equalsIgnoreCase(selfPlayerId)) {
                selfPlayer = registeredPlayer;
                break;
            }
        }


        if (selfPlayer == null) {
            throw new NullPointerException("Self Player could not be found! (" + registeredPlayers.size() + " players)");
        }


        // Well, since everything's complete and it's obvious it's working,
        // imma now gonna implement the actual party members to each party
        // object.

        for (final Player registeredPlayer : registeredPlayers) {
            final Party party = registeredPlayer.party();
            if (party == null) continue;

            for (int i = 0; i < party.members().length; i++) {
                final Player[] members = party.members();

                if (members[i] == null) {
                    // Found free slot

                    members[i] = registeredPlayer;
                    break;
                }
            }
        }


        // Refresh database of seen players
        final DatabaseHandler seenPlayersDatabase = Database.SEEN_PLAYERS.get();
        for (final Player registeredPlayer : registeredPlayers) {
            // Update database and read known info
        }


        final Game constructedGame = new Game(
                selfPlayer,
                state,
                registeredPlayers.toArray(new Player[0]),
                new Match(
                        matchId,
                        map,
                        null,
                        null
                ),
                map,
                server
        );


        return VIT.get().getEventManager()
                .call(new GameInitializedEvent(state, constructedGame))
                .getGame();
    }


    /**
     * Constructs a {@link Match} object for the match history
     * of a certain player.
     *
     * @param playerId ID of the player playing in that match whose information is relevant.
     * @param gainedRR Gained RR during match.
     * @param matchId Match id.
     * @param matchDetails JSONObject containing match details.
     *
     * @return Constructed {@link Match} object.
     */
    private Match constructMatch(
            final String playerId,
            final int gainedRR,
            final String matchId,
            final JSONObject matchDetails
    ) {

        int headShots = 0, bodyShots = 0, legShots = 0;

        final JSONObject matchInfo = matchDetails.getJSONObject("matchInfo");

        final boolean ranked = matchInfo.getBoolean("isRanked");

        // e.g: /Game/GameModes/Bomb/BombGameMode.BombGameMode_C
        // I'm not sure what exactly that means, but let's check it
        // some other day idk. Maybe it's on the valorant-api page?
        final String gameMode = matchInfo.getString("gameMode");


        final String mapUrl = matchInfo.getString("mapId");

        final String seasonId = matchInfo.getString("seasonId");
        final Season season = Season.getSeasonById(seasonId);


        final JSONArray teams = matchDetails.getJSONArray("teams");

        final JSONObject team1 = teams.getJSONObject(0);
        final JSONObject team2 = teams.getJSONObject(1);

        final String team1Id = team1.getString("teamId");

        final boolean team1Won = team1.getBoolean("won");
        final boolean team2Won = team2.getBoolean("won");

        final int team1Wins = team1.getInt("roundsWon");
        final int team2Wins = team2.getInt("roundsWon");


        // Prevent team of the player whose match it is.
        String playerTeamId = "";
        for (final Object playerObj : matchDetails.getJSONArray("players")) {
            final JSONObject player = (JSONObject) playerObj;
            final String teamId = player.getString("teamId");

            if (player.getString("subject").equalsIgnoreCase(playerId)) {
                playerTeamId = teamId;
                break;
            }
        }


        final JSONArray roundResults = matchDetails.getJSONArray("roundResults");

        for (final Object roundObj : roundResults) {
            final JSONObject round = (JSONObject) roundObj;
            final JSONArray playerStats = round.getJSONArray("playerStats");


            for (final Object statsObj : playerStats) {
                final JSONObject stats = (JSONObject) statsObj;
                final String matchPlayerId = stats.getString("subject");


                if (matchPlayerId.equalsIgnoreCase(playerId)) {
                    continue;
                }


                final JSONArray damageDetails = stats.getJSONArray("damage");

                for (final Object damageDetailObj : damageDetails) {
                    final JSONObject damageDetail = (JSONObject) damageDetailObj;


                    headShots += damageDetail.getInt("headshots");
                    bodyShots += damageDetail.getInt("bodyshots");
                    legShots += damageDetail.getInt("legshots");

                }
            }
        }



        final boolean isTeam1 = playerTeamId.equalsIgnoreCase(team1Id);

        final int wonRounds = isTeam1 ? team1Wins : team2Wins;
        final int lostRounds = isTeam1 ? team2Wins : team1Wins;

        final boolean won = isTeam1 ? team1Won : team2Won;


        final float headShotRate = (float) headShots / (float) (headShots + bodyShots + legShots);


        return new Match(
                matchId,
                MatchMap.getMapByUrl(mapUrl),
                new MatchInfo(
                        season, headShotRate,
                        headShots, bodyShots, legShots,
                        wonRounds, lostRounds, won
                ),
                new CompMatchResult(
                        gainedRR
                )
        );
    }



    /**
     * Takes an array of players and fetches each of their names if allowed.
     * Returns a modified version of the array with all player names.
     *
     * @param players Array of players to fetch their names from.
     *
     * @return New array of all players with their fetched player names.
     */
    @Override
    public Player[] updatePlayerNames(final Player... players) {
        final JSONArray array = new JSONArray();
        for (final Player player : players) {
            final PreFetchPlayerNameEvent preFetchPlayerNameEvent = new PreFetchPlayerNameEvent(
                    player.id(), player.settings().incognito()
            );

            if (preFetchPlayerNameEvent.isIncognito()) {
                continue;
            }

            array.put(player.id());
        }

        final JSONArray response = Requests.Send.Player.sendPlayerNameRequest(client, array);
        if (response == null) {
            throw new NullPointerException("Failed to update player names.");
        }


        final Map<String, String> playerNamesMap = new HashMap<>();

        for (int i = 0; i < response.length(); i++) {
            final JSONObject player = response.getJSONObject(i);

            final String playerId = player.getString("Subject");
            final String playerName = player.getString("GameName") + "#" + player.getString("TagLine");

            playerNamesMap.put(playerId, playerName);
        }

        for (int i = 0; i < response.length(); i++) {
            final Player player = players[i];
            final String name = playerNamesMap.get(player.id());

            players[i] = new Player(
                    player.id(),
                    player.team(),
                    name,
                    player.agent(),
                    player.level(),
                    player.playerCardId(),
                    player.playerTitleId(),
                    player.settings(),
                    player.inventory(),
                    player.competitive(),
                    player.stats(),
                    null,
                    player.playedMatches()
            );
        }

        return players;
    }



    /**
     * Creates a map of all player ids and maps them
     * with their corresponding weapon skin inventory.
     *
     * @param loadouts Loadout JSONArray
     *
     * @return Map of all player ids anf their player inventories.
     */
    private Map<String, PlayerInventory> fetchInventory(final JSONArray loadouts) {

        // PlayerId, Skin inventory
        final Map<String, PlayerInventory> playerInventories = new HashMap<>();

        for (final Object obj : loadouts) {
            final JSONObject loadout = ((JSONObject) obj).getJSONObject("Loadout");
            final JSONObject items = loadout.getJSONObject("Items");
            final String playerId = loadout.getString("Subject");

            // Weapon, Skin id
            final Map<Weapon, String> skins = new HashMap<>();

            for (final Weapon weapon : Weapon.values()) {
                final String gunId = weapon.getDefaultSkinId();

                if (loadout.has(gunId)) {
                    skins.put(weapon, gunId);
                    continue;
                }

                String skinId = items.getJSONObject(gunId)
                        .getJSONObject("Sockets")
                        .getJSONObject("bcef87d6-209b-46c6-8b19-fbe40bd95abc")
                        .getJSONObject("Item")
                        .getString("ID");

                skins.put(weapon, skinId);
            }

            playerInventories.put(playerId, new PlayerInventory(skins));
        }

        return playerInventories;
    }

    /**
     * Updates season activity.
     */
    private void updateSeasonActivity() {

        if (!Season.getActiveSeasons().isEmpty()) {
            return;
        }


        final JSONObject content = Requests.Get.Content.getContent(client);

        if (content == null) {
            throw new RuntimeException("Failed to fetch seasons! (Actually failed to receive content)");
        }

        final JSONArray seasonsArray = content.getJSONArray("Seasons");

        for (Object seasonObj : seasonsArray) {
            final JSONObject seasonJson = (JSONObject) seasonObj;
            final boolean active = seasonJson.getBoolean("IsActive");

            if (!active) {
                continue;
            }


            final String seasonId = seasonJson.getString("ID");
            final Season season = Season.getSeasonById(seasonId);

            if (season == null) {
                throw new NullPointerException("Failed to find season with ID: " + seasonId + "!");
            }

            Season.setActive(season);
        }
    }



    /**
     * Constructs the {@link PlayerCompetitive} object.
     *
     * @param lastMatch Latest match data.
     * @param compRequirements Competitive requirements.
     * @param competitive Competitive JSONObject.
     *
     * @return Constructed PlayerCompetitive.
     */
    private PlayerCompetitive constructPlayerCompetitive(
            final LastCompMatch lastMatch,
            final CompRequirements compRequirements,
            final JSONObject competitive
    ) {

        SeasonTiers seasonTiers = null;

        // Trying to fetch the tiers and seasons of the player.
        if (competitive.has("SeasonalInfoBySeasonID")) {
            final Object seasonInfoObj = competitive.get("SeasonalInfoBySeasonID");

            if (seasonInfoObj instanceof JSONObject seasonInfo) {

                // Season, Tier id
                final HashMap<SeasonStats, Integer> seasonTierIdsMap = new HashMap<>();


                // Read and set all seasons and tiers for the player.
                for (final String seasonId : seasonInfo.keySet()) {
                    final JSONObject seasonJson = seasonInfo.getJSONObject(seasonId);
                    final Season season = Season.getSeasonById(seasonId);


                    if (seasonJson == null) {
                        throw new RuntimeException("Failed to find season with ID: " + seasonId);
                    }


                    final int rr = seasonJson.getInt("RankedRating");
                    final int playedGames = seasonJson.getInt("NumberOfGames");
                    final int wonGames = seasonJson.getInt("NumberOfWins");
                    final int lostGames = playedGames - wonGames;

                    final float winRate = 100f * ((float) wonGames / (float) playedGames);


                    final SeasonStats seasonStats = new SeasonStats(
                            season,
                            rr,
                            playedGames,
                            winRate,
                            wonGames,
                            lostGames
                    );


                    if (Season.isActive(season)) {
                        if (seasonJson.has("CompetitiveTier")) {
                            final int tierId = seasonJson.getInt("CompetitiveTier");
                            seasonTierIdsMap.put(seasonStats, tierId);
                        }
                    } else {
                        if (seasonJson.has("Rank")) {
                            final int competitiveRankId = seasonJson.getInt("Rank");
                            seasonTierIdsMap.put(seasonStats, competitiveRankId);
                        }
                    }
                }


                // Create the SeasonTiers object.
                seasonTiers = new SeasonTiers(seasonTierIdsMap);
            }
        }


        // Check for current tier of the player.
        // First checking the episode if there's something.
        // If nothing is found there, the act is checked next.
        // If both have no result, {@link Tier#UNRANKED} with 0 RR
        // is returned.

        Tier currentTier = Tier.UNRANKED;
        int rr = 0;


        if (seasonTiers != null && compRequirements.rankedIn()) {
            final Season season = Season.getActiveAct();
            currentTier = seasonTiers.getTierInSeason(season);

            if (currentTier != Tier.UNRANKED) {
                rr = seasonTiers.getSessionStats(season).rr();
            }
        }


        return new PlayerCompetitive(
                currentTier,
                rr,
                seasonTiers,
                lastMatch,
                compRequirements
        );
    }


    /**
     * In order to avoid spamming the VALORANT servers,
     * it would be best to stop the program for a short
     * time.
     *
     * @param milliseconds Waiting time in milliseconds. (1000ms -> 1s)
     */
    private void wait(final int milliseconds) {

        if (milliseconds <= 0) {
            return;
        }

        try { Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
    }
}