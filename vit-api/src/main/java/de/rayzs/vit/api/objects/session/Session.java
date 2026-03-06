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

public class Session {

    private final File lockfile;

    private HttpClient client;
    private String selfPlayerId;

    public Session() {
        this.lockfile = FileDir.VALORANT_CONF.getFile("lockfile");
    }


    /**
     * Initializes all required information
     * to send requests. Should not be called
     * during runtime once it already has been called,
     * unless VALORANT has been restarted.
     */
    public void initialize() {
        this.client = Request.createClient();

        fetchAuthToken();
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
     * If VALORANT is currently open.
     *
     * @return True if VALORANT is open. False otherwise.
     */
    public boolean isOpen() {
        return this.lockfile.exists();
    }



    /**
     * Fetch and set the auth token.
     */
    private void fetchAuthToken() {

        if (!isOpen()) {
            throw new IllegalStateException("Cannot set access token while VALORANT isn't open...");
        }

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

        if (!isOpen()) {
            throw new IllegalStateException("Cannot fetch the request regions while VALORANT isn't even open... I mean I could, but what's the point?");
        }


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

        final JSONObject json = new JSONObject(request.sendAndGet(client));

        final String accessToken = json.getString("accessToken");
        final String entitlementToken = json.getString("token");

        // Own player's id.
        this.selfPlayerId = json.getString("subject");

        Request.setHeaders(accessToken, entitlementToken, currentVersion);
    }


    /**
     * Check if the client is currently in-game
     * or not.
     *
     * @return True if the client is in a match. False otherwise.
     */
    public boolean insideMatch() {

        if (!isOpen()) {
            return false;
        }


        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.LOCAL,
                "chat/v4/presences"
        );

        final JSONArray presences = new JSONObject(request.sendAndGet(client)).getJSONArray("presences");

        for (Object presenceObj : presences) {
            final JSONObject presence = (JSONObject) presenceObj;

            final String product = presence.getString("product");
            final String playerId = presence.getString("playerId");

            if (!product.equalsIgnoreCase("valorant") || !playerId.equalsIgnoreCase(selfPlayerId)) {
                continue;
            }

            final String encodedPrivate = presence.getString("private");
            final String decodedPrivate = new String(Base64.getDecoder().decode(encodedPrivate), StandardCharsets.UTF_8);

            final JSONObject presenceData = new JSONObject(decodedPrivate);

            try {
                final JSONObject matchPresenceData = presence.getJSONObject("matchPresenceData");
                return !matchPresenceData.getString("sessionLoopState").equalsIgnoreCase("MENUS");
            } catch (JSONException exception) {
                return !presenceData.getString("sessionLoopState").equalsIgnoreCase("MENUS");
            }
        }

        return false;
    }
}
