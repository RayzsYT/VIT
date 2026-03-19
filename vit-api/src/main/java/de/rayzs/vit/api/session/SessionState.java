package de.rayzs.vit.api.session;

import java.util.Locale;

public enum SessionState {
    VALORANT_NOT_OPEN   (false,     false,      "",                 2500),
    IN_MENU             (true,      false,      "",                 1500),
    IN_LOBBY            (true,      true,       "pregame",          2500),
    IN_GAME             (true,      true,       "core-game",        5000);


    private final boolean started, insideMatch;
    private final String internalName;
    private final int waitingInMillis;

    SessionState(
            final boolean started,
            final boolean insideMatch,
            final String internalName,
            final int waitingInMillis
    ) {
        this.started = started;
        this.insideMatch = insideMatch;
        this.internalName = internalName;
        this.waitingInMillis = waitingInMillis;
    }

    /**
     * Whether the game is even
     * started or not.
     *
     * @return True if the game is started. False otherwise.
     */
    public boolean isValorantStarted() {
        return this.started;
    }

    /**
     * Whether the client is currently
     * in a match or not.
     *
     * @return True if the client is in a match. False otherwise.
     */
    public boolean isInsideMatch() {
        return insideMatch;
    }

    /**
     * Get the name how VALORANT names and use it in
     * their API. I did it this way, because I did not like
     * the original names. So I renamed them in a more
     * recognizable way. (Just personal preference xd)
     *
     * @return Get internal name how VALORANT actually calls them inside their API.
     */
    public String getInternalName() {
        return this.internalName;
    }

    /**
     * Get time in millis to wait
     * 'til the next tick.
     *
     * @return Waiting in millis.
     */
    public int getWaitingInMillisTime() {
        return waitingInMillis;
    }

    /**
     * Get the SessionState from the loop state.
     *
     * @param loopState Loop state
     * @return SessionState or null.
     */
    public static SessionState from(final String loopState) {

        switch (loopState.toUpperCase(Locale.ROOT)) {
            case "MENUS" -> {
                return SessionState.IN_MENU;
            }

            case "PREGAME" -> {
                return SessionState.IN_LOBBY;
            }
            case "INGAME" -> {
                return SessionState.IN_GAME;
            }
        }

        return null;
    }
}
