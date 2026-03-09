package de.rayzs.vit.api.session;

import de.rayzs.vit.api.objects.game.Game;
import java.net.http.HttpClient;
import java.util.function.Consumer;

public interface Session {


    int MATCH_HISTORY_NUM = 5;


    /**
     * Initializes all required information
     * to send requests. Should not be called
     * during runtime once it already has been called,
     * unless VALORANT has been restarted.
     */
    void initialize();


    /**
     * Get the client to send requests
     * to the VALORANT servers.
     *
     * @return HttpClient.
     */
    HttpClient getClient();


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
    SessionState getSessionState();



    /**
     * Construct the game object which loads
     * all players and the map being played on.
     *
     * @param state Current session state.
     * @param playerLoadConsumer A consumer with the amount of currently loaded players.
     *
     * @return Constructed {@link Game} object.
     */
    Game constructGame(
            final SessionState state,
            final Consumer<Integer> playerLoadConsumer
    );
}