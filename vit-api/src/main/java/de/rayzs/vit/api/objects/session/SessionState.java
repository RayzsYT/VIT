package de.rayzs.vit.api.objects.session;

public enum SessionState {
    VALORANT_NOT_OPEN   (false, false),
    IN_MENU             (true, false),
    IN_GAME             (true, true);


    private final boolean started, insideMatch;
    SessionState(final boolean started, final boolean insideMatch) {
        this.started = started;
        this.insideMatch = insideMatch;
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
}
