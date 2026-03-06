package de.rayzs.vit.api.objects.game;

public enum GameState {
    LOBBY           ("pregame"),
    IN_GAME         ("core-game");


    private final String internalName;
    GameState(final String internalName) {
        this.internalName = internalName;
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
        return internalName;
    }
}
