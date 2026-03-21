package de.rayzs.vit.api.session;

import de.rayzs.vit.api.event.events.game.PreGameInitializeEvent;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.player.Player;

import java.net.http.HttpClient;
import java.util.function.Consumer;

public interface Session {


    // Cooldowns
    int PER_PLAYER_COOLDOWN = 250;
    int PER_MATCH_COOLDOWN = 50;


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
    SessionState fetchSessionState();



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
    Game constructGame(
            final SessionState state,
            final Consumer<PreGameInitializeEvent> preGameConsumer,
            final Consumer<Integer> playerLoadConsumer
    );

    /**
     * Takes an array of players and fetches each of their names if allowed.
     * Returns a modified version of the array with all player names.
     *
     * @param players Array of players to fetch their names from.
     *
     * @return New array of all players with their fetched player names.
     */
    Player[] updatePlayerNames(
            final Player... players
    );
}