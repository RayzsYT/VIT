package de.rayzs.vit.api.request;

import de.rayzs.vit.api.objects.items.AvailableItem;
import de.rayzs.vit.api.session.SessionState;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.util.Optional;

public class Requests {
    private Requests() {}


    /**
     * Premade request to receive data.
     */
    public static class Get {
        private Get() {}


        /**
         * Match data
         */
        public static class Match {
            private Match() {}



            /**
             * Fetches initial data of the match based on its match id.
             * <p>
             * If the state is {@link SessionState#IN_GAME}, it will
             * call and return the result of {@link #fetchCoreMatchData(HttpClient, String)}.
             * <p>
             * If the state is {@link SessionState#IN_LOBBY}, it will
             * call and return the result of {@link #fetchPreGameMatchData(HttpClient, String)}.
             * <p>
             * Will return NULL in any other case.
             */
            public static JSONObject fetchMatchData(final HttpClient client, final SessionState state, final String selfPlayerId) {
                return switch (state) {
                    case IN_LOBBY -> fetchPreGameMatchData(client, selfPlayerId);
                    case IN_GAME -> fetchCoreMatchData(client, selfPlayerId);
                    default -> null;
                };
            }

            /**
             * Fetches initial data of a running match containing its match id.
             */
            public static JSONObject fetchCoreMatchData(final HttpClient client, final String selfPlayerId) {
                return getAsJsonObject(
                        client,
                        RequestDest.GLZ,
                        "core-game/v1/players/" + selfPlayerId
                );
            }

            /**
             * Fetches initial data of a not yet running match containing its match id. (e.g: agent selection)
             */
            public static JSONObject fetchPreGameMatchData(final HttpClient client, final String selfPlayerId) {
                return getAsJsonObject(
                        client,
                        RequestDest.GLZ,
                        "pregame/v1/players/" + selfPlayerId
                );
            }


            /**
             * Fetches first details of a running match. Contains the participating players, their teams,
             * the current map, as well as the server.
             */
            public static JSONObject fetchLiveMatchDetails(final HttpClient client, final SessionState state, final String matchId) {
                return getAsJsonObject(
                        client,
                        RequestDest.GLZ,
                        state.getInternalName() + "/v1/matches/" + matchId
                );
            }


            /**
             * Fetches details of a past match. Contains the participating players, their teams,
             * the current map, damage data, and the results per round, as well as the final results
             * of the match outcome.
             */
            public static JSONObject fetchPastMatchDetails(final HttpClient client, final String matchId) {
                return getAsJsonObject(
                        client,
                        RequestDest.PD,
                        "match-details/v1/matches/" + matchId
                );
            }


            /**
             * Fetch the latest match ids from a certain index up to a certain index.
             */
            public static JSONObject fetchMatchHistory(final HttpClient client, final String playerId, final int fromIndex, final int toIndex) {
                return getAsJsonObject(
                        client,
                        RequestDest.PD,
                        "mmr/v1/players/" + playerId + "/competitiveupdates?startIndex=" + fromIndex + "&endIndex=" + toIndex + "&queue=competitive"
                );
            }

        }



        public static class Player {
            private Player() {}


            /**
             * Fetches the loadout of all players inside the match. Basically the selected skin weapons.
             */
            public static JSONArray fetchPlayerLayouts(final HttpClient client, final SessionState state, final String matchId) {
                final JSONObject playerLayouts = getAsJsonObject(
                        client,
                        RequestDest.GLZ,
                        state.getInternalName() + "/v1/matches/" + matchId + "/loadouts"
                );

                return playerLayouts != null ? playerLayouts.getJSONArray("Loadouts") : null;
            }


            /**
             * Fetches player competitive information.
             */
            public static JSONObject fetchPlayersMMR(final HttpClient client, final String playerId) {
                return getAsJsonObject(
                        client,
                        RequestDest.PD,
                        "mmr/v1/players/" + playerId
                );
            }


            /**
             * Fetch the party id the player belongs to.
             */
            public static JSONObject fetchPlayerParty(final HttpClient client, final String playerId) {
                return getAsJsonObject(
                        client,
                        RequestDest.GLZ,
                        "/parties/v1/players/" + playerId
                );
            }


            /**
             * Get available items from a player.
             * For example to find out what agents a player has unlocked.
             */
            public static JSONObject fetchPlayerAvailableItem(final HttpClient client, final String playerId, final AvailableItem availableItem) {
                return getAsJsonObject(
                        client,
                        RequestDest.PD,
                        "/store/v1/entitlements/" + playerId + "/" + availableItem.getId()
                );
            }
        }



        public static class Content {
            private Content() {}


            /**
             * Current content like active seasons etc... (Haven't checked any further actually)
             */
            public static JSONObject getContent(final HttpClient client) {
                return getAsJsonObject(
                        client,
                        RequestDest.SHARED,
                        "content-service/v3/content"
                );
            }
        }

    }


    /**
     * Premade requests to send data.
     */
    public static class Send {
        private Send() {}



        public static class Match {
            private Match() {}


            /**
             * Quit pre-game match. (Basically dodge.)
             * @return TRUE if successful. FALSE otherwise.
             */
            public static boolean quitPreGameMatch(final HttpClient client, final String matchId) {
                return sendAndGetAsString(
                        client,
                        RequestDest.GLZ,
                        "/pregame/v1/matches/" + matchId + "/quit",
                        ""
                ) != null;
            }


            /**
             * Select an agent during agent selection phase.
             */
            public static boolean selectAgent(final HttpClient client, final String matchId, final String agentId) {
                return sendAndGetAsString(client,
                        RequestDest.GLZ,
                        "/pregame/v1/matches/" + matchId + "/select/" + agentId,
                        ""
                ) != null;
            }


            /**
             * Lock in an agent during agent selection phase.
             * Might be funny to use in something like a random agent selector. :D
             */
            public static boolean lockAgent(final HttpClient client, final String matchId, final String agentId) {
                return sendAndGetAsString(client,
                        RequestDest.GLZ,
                        "/pregame/v1/matches/" + matchId + "/lock/" + agentId,
                        ""
                ) != null;
            }

        }



        public static class Player {
            private Player() {}


            /**
             * Sends a request to receive the names of the players
             * whose player-ids are written inside the {@link JSONArray}.
             */
            public static JSONArray sendPlayerNameRequest(final HttpClient client, final JSONArray playerIds) {
                return sendAndGetAsJsonArray(
                        client,
                        RequestDest.PD,
                        "name-service/v2/players",
                        playerIds.toString()
                );
            }
        }

    }






    // Some utils for me to use within this class.
    // Has no need to exist outside of this class anyway.



    // Get


    /**
     * Sends a {@link RequestMethod#GET} request and returns the result as {@link JSONObject}.
     */
    private static JSONObject getAsJsonObject(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath
    ) {
        final String result = getAsString(client, dest, urlPath);

        return result != null ? new JSONObject(result) : null;
    }

    /**
     * Sends a {@link RequestMethod#GET} request and returns the result as {@link JSONArray}.
     */
    private static JSONArray getAsJsonArray(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath
    ) {
        final String result = getAsString(client, dest, urlPath);

        return result != null ? new JSONArray(result) : null;
    }

    /**
     * Sends a {@link RequestMethod#GET} request and returns the result as raw {@link String}.
     */
    private static String getAsString(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath
    ) {
        final Request request = Request.createRequest(
                RequestMethod.GET,
                dest,
                urlPath
        );

        final Optional<String> result = request.sendAndGet(client);
        return result.orElse(null);
    }


    // Send


    /**
     * Sends a {@link RequestMethod#PUT} request and returns the result as {@link JSONObject}.
     */
    private static JSONObject sendAndGetAsJsonObject(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        final String result = sendAndGetAsString(client, dest, urlPath, body);

        return result != null ? new JSONObject(result) : null;
    }

    /**
     * Sends a {@link RequestMethod#PUT} request and returns the result as {@link JSONArray}.
     */
    private static JSONArray sendAndGetAsJsonArray(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        final String result = sendAndGetAsString(client, dest, urlPath, body);

        return result != null ? new JSONArray(result) : null;
    }

    /**
     * Sends a {@link RequestMethod#PUT} request and returns the result as raw {@link String}.
     */
    private static String sendAndGetAsString(
            final HttpClient client,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        final Request request = Request.createRequest(
                RequestMethod.PUT,
                dest,
                urlPath,
                body
        );

        final Optional<String> result = request.sendAndGet(client);
        return result.orElse(null);
    }
}
