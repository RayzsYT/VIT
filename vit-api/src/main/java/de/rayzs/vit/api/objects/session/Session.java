package de.rayzs.vit.api.objects.session;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import de.rayzs.vit.api.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;

public class Session {

    private final File lockfile;

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
                        parts[2] = "shipping";

                        currentVersion = String.join("-", parts);
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
            throw new NullPointerException("Request failed!");
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

            return sessionLoopState.equalsIgnoreCase("menus")
                    ? SessionState.IN_MENU
                    : SessionState.IN_GAME;
        }

        return SessionState.VALORANT_NOT_OPEN;
    }
}
