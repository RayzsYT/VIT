package de.rayzs.vit.launch.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.events.gui.UpdateMainGuiEvent;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.launch.screens.game.LiveScreen;
import de.rayzs.vit.launch.screens.game.LobbyScreen;
import de.rayzs.vit.launch.screens.other.InactiveScreen;
import de.rayzs.vit.launch.screens.other.LoadingScreen;

import java.util.function.Consumer;

public class MainGuiUpdater {

    private InactiveScreen inactiveScreen;
    private LoadingScreen loadingScreen;
    private LobbyScreen lobbyScreen;
    private LiveScreen liveScreen;

    private final VITAPI api;
    private final MainGUI gui;

    public MainGuiUpdater(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.gui = gui;


        if (this.gui == null) {
            return;
        }


        this.inactiveScreen = new InactiveScreen();
        this.loadingScreen = new LoadingScreen();
        this.lobbyScreen = new LobbyScreen();
        this.liveScreen = new LiveScreen();

        this.gui.setAlwaysOnTop(true);
        this.gui.setAlwaysOnTop(false);
    }

    public void handle(final SessionState state) {
        if (gui == null) return; // Ignore if gui is not set!



        final UpdateMainGuiEvent updateMainGuiEvent = api.getEventManager().call(
                new UpdateMainGuiEvent(state, gui)
        );

        if (updateMainGuiEvent.isCancelled()) {
            return;
        }

        loadingScreen.resetText();



        if (!state.isInsideMatch()) resetAllEntireCaches();
        else loadingScreen.load(api, gui);


        switch (state) {

            case VALORANT_NOT_OPEN -> {
                inactiveScreen.load(api, gui);
            }

            case IN_MENU -> {
                loadingScreen.load(api, gui);
            }

            case IN_GAME -> {
                // Remove player banners and all other
                // elements like images.
                lobbyScreen.clearEntireCache();
            }
        }

    }

    /**
     * Updates main gui.
     *
     * @param mainGuiConsumer Consumer to update main gui.
     */
    public void updateGUI(
            final Consumer<MainGUI> mainGuiConsumer
    ) {
        if (gui == null) return; // Ignore if gui is not set!


        mainGuiConsumer.accept(gui);
    }

    /**
     * Updates the loading screen.
     *
     * @param loadingScreenConsumer Consumer to update loading screen.
     */
    public void updateLoadingScreen(
            final Consumer<LoadingScreen> loadingScreenConsumer
    ) {
        if (gui == null) return; // Ignore if gui is not set!


        loadingScreenConsumer.accept(loadingScreen);
    }

    /**
     * Loads the in-game live screen.
     *
     * @param state Session state.
     */
    public void loadGameScreen(final SessionState state) {
        if (gui == null) return; // Ignore if gui is not set!


        if (api.getGame() != null) {
            throw new NullPointerException("No running game instance found!");
        }

        if (state == SessionState.IN_LOBBY) {
            lobbyScreen.load(api, gui);
        } else {
            liveScreen.load(api, gui);
        }
    }

    /**
     * Reset cache of lobby or live screen
     * and all its evolved images to free
     * some memory.
     */
    private void resetAllEntireCaches() {
        if (gui == null) return; // Ignore if gui is not set!


        liveScreen.clearEntireCache();
        lobbyScreen.clearEntireCache();
    }
}
