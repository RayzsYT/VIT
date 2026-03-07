package de.rayzs.vit.api.objects.session;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.game.GameState;
import de.rayzs.vit.api.objects.items.*;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.PlayerCompetitive;
import de.rayzs.vit.api.objects.player.PlayerInventory;
import de.rayzs.vit.api.objects.player.PlayerSettings;
import de.rayzs.vit.api.objects.player.competitive.CompRequirements;
import de.rayzs.vit.api.objects.player.competitive.MatchData;
import de.rayzs.vit.api.objects.player.competitive.SeasonStats;
import de.rayzs.vit.api.objects.player.competitive.SeasonTiers;
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
import java.util.function.Consumer;

public class Session {

    private final File lockfile;

    // SeasonId, Season
    private final Map<String, Season> seasons = new HashMap<>();

    private Season currentSeason;
    private HttpClient client;
    private String selfPlayerId;

    public Session() {
        this.lockfile = FileDir.VALORANT_CONF.getFile("lockfile");

        fetchAuthToken();
    }


    /**
     * Initializes all required information
     * to send requests. Should not be called
     * during runtime once it already has been called,
     * unless VALORANT has been restarted.
     */
    public void initialize() {
        this.client = Request.createClient();

        fetchRequestHeaders(this.client);
    }


    /**
     * Get the client to send requests
     * to the VALORANT servers.
     *
     * @return HttpClient.
     */
    public HttpClient getClient() {
        return this.client;
    }



    /**
     * Fetch and set the auth token.
     */
    private void fetchAuthToken() {
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
     * Fetches and sets the headers for creating requests
     * to the VALORANT servers.
     *
     * @param client HttpClient sending the request.
     */
    private void fetchRequestHeaders(final HttpClient client) {
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
                        if (partsList.size() >= 2) {
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


        RequestDest.SHARED.update(firstRegion);
        RequestDest.PD.update(firstRegion);

        RequestDest.GLZ.update(secondRegion, firstRegion);


        // Sending request to receive the required headers
        // for sending requests towards the VALORANT servers.
        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.LOCAL,
                "entitlements/v1/token"
        );

        final Optional<String> result = request.sendAndGet(client);

        if (result.isEmpty()) {
            throw new NullPointerException("Request failed!");
        }

        final JSONObject json = new JSONObject(result.get());

        final String accessToken = json.getString("accessToken");
        final String entitlementToken = json.getString("token");

        // Own player's id.
        this.selfPlayerId = json.getString("subject");

        Request.setHeaders(accessToken, entitlementToken, currentVersion);
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
    public SessionState getSessionState() {
        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.LOCAL,
                "chat/v4/presences"
        );


        final Optional<String> result = request.sendAndGet(client);

        if (result.isEmpty()) {
            return SessionState.VALORANT_NOT_OPEN;
        }


        final JSONArray presences = new JSONObject(result.get()).getJSONArray("presences");

        for (Object presenceObj : presences) {
            final JSONObject presence = (JSONObject) presenceObj;

            final String product = presence.getString("product");
            final String playerId = presence.getString("puuid");

            if (!product.equalsIgnoreCase("valorant") || !playerId.equalsIgnoreCase(selfPlayerId)) {
                continue;
            }


            // Sometimes exactly when VALORANT boots and the tracker
            // is enabled, the private key isn't fully build yet.
            // So to avoid any confusions or any issues, we'll simply ignore
            // it in that exact time and wait for the next iteration.
            final Object encodedPrivateObj = presence.get("private");
            if (! (encodedPrivateObj instanceof String encodedPrivate)) {
                continue;
            }

            final String decodedPrivate = new String(Base64.getDecoder().decode(encodedPrivate), StandardCharsets.UTF_8);

            final JSONObject presenceData = new JSONObject(decodedPrivate);
            final JSONObject matchPresenceData = presenceData.getJSONObject("matchPresenceData");

            final String sessionLoopState = matchPresenceData.getString("sessionLoopState");

            System.out.println("=> " + sessionLoopState);

            return sessionLoopState.equalsIgnoreCase("menus")
                    ? SessionState.IN_MENU
                    : SessionState.IN_GAME;
        }

        return SessionState.VALORANT_NOT_OPEN;
    }



    /**
     * Construct the game object which loads
     * all players and the map being played on.
     *
     * @param playerLoadConsumer A consumer with the amount of currently loaded players.
     *
     * @return Constructed {@link Game} object.
     */
    public Game constructGame(final Consumer<Integer> playerLoadConsumer) {

        fetchSeasons(); // Fetches all available seasons first, in case they were not fetched yet.


        // Players that are in incognito mode.
        List<String> incognitoPlayerIds = new ArrayList<>();
        List<Player> registeredPlayers = new ArrayList<>();

        GameState gameState;
        String matchId, mapId, server;

        PlayerCompetitive playerCompetitive;


        // Fetch match id

        final Request matchRequest = Request.createRequest(
                RequestMethod.GET,
                RequestDest.GLZ,
                "core-game/v1/players/" + selfPlayerId
        );


        final Optional<String> matchResult = matchRequest.sendAndGet(client);
        if (matchResult.isPresent()) {
            gameState = GameState.IN_GAME;
            matchId = new JSONObject(matchResult.get()).getString("MatchID");
        } else {

            // Checking their pre-game match. This is practically just
            // the lobby where just select your agents usually.

            final Request pregameRequest = Request.createRequest(
                    RequestMethod.GET,
                    RequestDest.GLZ,
                    "pregame/v1/players/" + selfPlayerId
            );

            final Optional<String> pregameResult = pregameRequest.sendAndGet(client);
            if (pregameResult.isEmpty()) {
                throw new NullPointerException("Failed to construct game object! Failed to fetch match id.");
            }

            gameState = GameState.LOBBY;
            matchId = new JSONObject(pregameResult.get()).getString("MatchID");
        }


        final Request matchDetailsRequest = Request.createRequest(
                RequestMethod.GET,
                RequestDest.GLZ,
                gameState.getInternalName() + "/v1/matches/" + matchId
        );

        final Optional<String> matchDetailsResult = matchDetailsRequest.sendAndGet(client);
        if (matchDetailsResult.isEmpty()) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch match details.");
        }


        // Fetch match information
        final JSONObject match = new JSONObject(matchDetailsResult.get());

        final String mapName = match.getString("MapID");
        mapId = VIT.get().getImageProvider().getMaps().getIdByName(mapName);


        String tmpServer = match.getString("GamePodID");
        tmpServer = tmpServer.substring(0, tmpServer.lastIndexOf("-"));
        tmpServer = tmpServer.substring(tmpServer.lastIndexOf("-") + 1);
        tmpServer = Character.toUpperCase(tmpServer.charAt(0)) + tmpServer.substring(1);

        server = tmpServer;



        // Fetch the loadouts of all players.

        final Request loadoutsRequest = Request.createRequest(
                RequestMethod.GET,
                RequestDest.GLZ,
                gameState.getInternalName() + "/v1/matches/" + matchId + "/loadouts"
        );

        final Optional<String> loadoutsResult = loadoutsRequest.sendAndGet(client);
        if (loadoutsResult.isEmpty()) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch match loadouts.");
        }


        final JSONArray loadouts = new JSONObject(loadoutsResult.get()).getJSONArray("Loadouts");
        // Player id, Skin inventory
        final Map<String, PlayerInventory> playerInventories = gameState == GameState.IN_GAME
                ? fetchInventory(loadouts)
                : Map.of();


        // Get list of all players.
        final JSONArray players = (gameState == GameState.LOBBY
                ? match.getJSONObject("AllyTeam") // Since it's the lobby, enemy team won't be shared yet.
                : match
        ).getJSONArray("Players");


        Team ownTeam;

        if (gameState == GameState.LOBBY) {
            final String teamId = match
                    .getJSONObject("AllyTeam")
                    .getString("TeamID");

            ownTeam = teamId.equalsIgnoreCase("blue")
                    ? Team.DEFEND : Team.ATTACK;
        }


        // Preparing a json of players whose names we want
        // to ask for.
        final JSONArray playersArray = new JSONArray();
        for (Object playerObj : players) {
            final JSONObject player = (JSONObject) playerObj;
            final String playerId = player.getString("Subject");

            /*
            In order to be RIOT compliant, incognito users won't be checked.
            Because it bypasses the streamer-protection:
            */

            final JSONObject identity = player.getJSONObject("PlayerIdentity");
            final boolean incognito = identity.getBoolean("Incognito");

            if (!incognito) {
                playersArray.put(playerId);
            } else incognitoPlayerIds.add(playerId);
        }


        // Asking VALORANT for the information of the players inside
        // our match.
        final Request playerNamesRequest = Request.createRequest(
                RequestMethod.PUT,
                RequestDest.PD,
                "name-service/v2/players",
                playersArray.toString()
        );

        final Optional<String> playerNameResult = playerNamesRequest.sendAndGet(client);
        if (playerNameResult.isEmpty()) {
            throw new NullPointerException("Failed to construct game object! Failed to fetch player names.");
        }


        final JSONArray playerNames = new JSONArray(playerNameResult.get());
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
            final String playerName = playerNamesMap.getOrDefault(playerId, "");


            // Competitive information about the player...
            final Request competitiveRequest = Request.createRequest(
                    RequestMethod.GET,
                    RequestDest.PD,
                    "mmr/v1/players/" + playerId
            );



            final Optional<String> competitiveResult = competitiveRequest.sendAndGet(client);
            if (competitiveResult.isPresent()) {
                final JSONObject rank =  new JSONObject(competitiveResult.get());

                final Object latestCompGameObj = rank.get("LatestCompetitiveUpdate");

                final JSONObject competitive = rank.getJSONObject("QueueSkills").getJSONObject("competitive");
                final int requiredRankGames = competitive.getInt("TotalGamesNeededForRating");
                final boolean rankedIn = requiredRankGames == 0;

                MatchData lastMatch = null;
                if (latestCompGameObj instanceof JSONObject latestCompGame) {
                    final String lastPlayedMapId = latestCompGame.getString("MapID");
                    final int lastReceivedRR = latestCompGame.getInt("RankedRatingEarned");

                    lastMatch = new MatchData(lastReceivedRR, lastPlayedMapId);
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


                final JSONObject identity = playerJson.getJSONObject("PlayerIdentity");

                final boolean incognito = incognitoPlayerIds.contains(playerId);
                final boolean levelHidden = identity.getBoolean("HideAccountLevel");
                final int level = identity.getInt("AccountLevel");

                final String playerCardId = identity.getString("PlayerCardID");
                final String playerTitleId = identity.getString("PlayerTitleID");

                final Team team = playerJson.getString("TeamID").equalsIgnoreCase("blue")
                        ? Team.DEFEND
                        : Team.ATTACK;

                final PlayerSettings settings = new PlayerSettings(
                        levelHidden,
                        incognito
                );

                final PlayerInventory inventory = playerInventories.get(playerId);


                final String agentId = gameState == GameState.IN_GAME
                        ? playerJson.getString("CharacterID")
                        : null;

                final Agent agent =  gameState == GameState.IN_GAME
                        ? Agent.getAgentById(agentId)
                        : null;


                registeredPlayers.add(new Player(
                        playerId,
                        team,
                        playerName,
                        agent,
                        level,
                        playerCardId,
                        playerTitleId,
                        settings,
                        inventory,
                        playerCompetitive
                ));

            } else {
                System.err.println("Failed to fetch competitive information of player: " + playerName + ". ( " + playerId + " )");
            }

            /*
            JSONArray playedMatches = new JSONArray();

            List<String> playedMatchesIds = new ArrayList<>();
            JSONArray matches;

            HttpRequest lastMatchesRequest = RequestHelper.createRequest(RequestHelper.RequestType.PD, "mmr/v1/players/" + otherPlayerId + "/competitiveupdates?startIndex=0&endIndex=" + 1 + "&queue=competitive");
            HttpResponse<String> lastMatchesResponse = client.send(lastMatchesRequest, HttpResponse.BodyHandlers.ofString());

            if (lastMatchesResponse.statusCode() == 200) {
                JSONObject matchHistoryStart = new JSONObject(lastMatchesResponse.body());

                if (matchHistoryStart.has("Matches")) {
                    matches = matchHistoryStart.getJSONArray("Matches");

                    for (Object matchObj : matches) {
                        JSONObject match = (JSONObject) matchObj;
                        String matchID = match.getString("MatchID");
                        playedMatchesIds.add(matchID);
                    }
                }

            } else System.out.println("Failed to get player headshot information for " + otherPlayerId + "!");

            if (!playedMatchesIds.isEmpty()) {

                for (String playedMatchId : playedMatchesIds) {

                    HttpRequest playedMatchDetailsRequest = RequestHelper.createRequest(RequestHelper.RequestType.PD, "match-details/v1/matches/" + playedMatchId);
                    HttpResponse<String> playedMatchDetailsResponses = client.send(playedMatchDetailsRequest, HttpResponse.BodyHandlers.ofString());

                    if (lastMatchesResponse.statusCode() == 200) {

                        if (playedMatchDetailsResponses.body().charAt(0) == '{') {
                            JSONObject playedMatchDetails = new JSONObject(playedMatchDetailsResponses.body());
                            playedMatches.put(playedMatchDetails);
                        }

                    }

                    Thread.sleep(200);
                }

             */
            }


        final Player selfPlayer = registeredPlayers.stream()
                .filter(player -> player.id().equalsIgnoreCase(selfPlayerId))
                .findFirst()
                .get();


        return new Game(
                selfPlayer,
                gameState,
                registeredPlayers.toArray(new Player[0]),
                mapId,
                server
        );
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
     * Fetches all seasons.
     */
    private void fetchSeasons() {

        if (!seasons.isEmpty()) {
            return;
        }

        final Request contentRequest = Request.createRequest(
                RequestMethod.GET,
                RequestDest.SHARED,
                "content-service/v3/content"
        );

        final Optional<String> contentResult = contentRequest.sendAndGet(client);
        if (contentResult.isEmpty()) {
            throw new RuntimeException("Failed to fetch seasons!");
        }

        final JSONArray seasonsArray = new JSONObject(contentResult.get()).getJSONArray("Seasons");

        String episodeName = "";
        for (Object seasonObj : seasonsArray) {
            final JSONObject seasonJson = (JSONObject) seasonObj;

            String seasonName = seasonJson.getString("Name");

            final String seasonType = seasonJson.getString("Type");
            final String seasonId = seasonJson.getString("ID");

            final boolean active = seasonJson.getBoolean("IsActive");


            if (seasonType.equals("episode")) {
                episodeName = seasonName;
            }

            if (!seasonName.equals(episodeName)) {
                seasonName = episodeName + " " + seasonName;
            }


            final Season season = new Season(
                    seasonId,
                    seasonName,
                    seasonType,
                    active
            );

            seasons.put(seasonId, season);

            if (active) {
                currentSeason = season;
            }
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
            final MatchData lastMatch,
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
                    final Season season = seasons.get(seasonId);


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


                    if (seasonJson.has("CompetitiveTier")) {
                        final int tierId = seasonJson.getInt("CompetitiveTier");
                        seasonTierIdsMap.put(seasonStats, tierId);
                    }

                    if (seasonJson.has("Rank")) {
                        final int competitiveRankId = seasonJson.getInt("Rank");
                        seasonTierIdsMap.put(seasonStats, competitiveRankId);
                    }

                }


                // Create the SeasonTiers object.
                seasonTiers = new SeasonTiers(seasonTierIdsMap);
            }
        }


        final Tier currentTier = seasonTiers != null
                ? seasonTiers.getTierInSeason(currentSeason)
                : Tier.UNRANKED;


        final int rr = seasonTiers != null
                ? seasonTiers.getSessionStats(currentSeason).rr()
                : 0;

        return new PlayerCompetitive(
                currentTier,
                rr,
                seasonTiers,
                lastMatch,
                compRequirements
        );
    }
}