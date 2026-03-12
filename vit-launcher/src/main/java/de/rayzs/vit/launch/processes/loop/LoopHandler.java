package de.rayzs.vit.launch.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.events.StateChangeEvent;
import de.rayzs.vit.api.event.events.UpdateMainGuiEvent;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.launch.gui.screens.InactiveScreen;
import de.rayzs.vit.launch.gui.screens.LiveScreen;
import de.rayzs.vit.launch.gui.screens.LoadingScreen;
import de.rayzs.vit.launch.gui.screens.LobbyScreen;

public class LoopHandler {

    private final VITAPI api;
    private final MainGUI gui;


    private final InactiveScreen inactiveScreen = new InactiveScreen();
    private final LoadingScreen loadingScreen = new LoadingScreen();
    private final LobbyScreen lobbyScreen = new LobbyScreen();
    private final LiveScreen liveScreen = new LiveScreen();

    private SessionState priorState;


    public LoopHandler(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.gui = gui;

        if (gui != null) {
            gui.setAlwaysOnTop(true);
            gui.setAlwaysOnTop(false);

            inactiveScreen.load(api, gui);
        }
    }


    public void handle() {
        if (!Request.areHeadersSet()) {
            try {
                api.getSession().initialize();
            } catch (Exception ignored) { return; }
        }


        final SessionState sessionState = api.getSession().getSessionState();


        if (!sessionState.isValorantStarted()) {
            Request.unsetAuthToken();
            Request.unsetHeaders();
        }


        if (priorState == sessionState) {
            return;
        }


        priorState = sessionState;



        final StateChangeEvent stateChangeEvent = api.getEventManager().call(new StateChangeEvent(priorState, sessionState));

        if (gui == null) {
            return;
        }



        final UpdateMainGuiEvent updateMainGuiEvent = api.getEventManager().call(
                new UpdateMainGuiEvent(stateChangeEvent.getState(), gui)
        );

        if (updateMainGuiEvent.isCancelled()) {
            return;
        }



        loadingScreen.resetText();


        switch (sessionState) {

            case VALORANT_NOT_OPEN -> {
                inactiveScreen.load(api, gui);
            }

            case IN_MENU -> {
                loadingScreen.load(api, gui);
            }

            case IN_LOBBY -> {
                loadingScreen.load(api, gui);
                loadLobbyScreen();
            }

            case IN_GAME -> {
                loadingScreen.load(api, gui);
                loadLiveScreen();
            }
        }
    }

    private void loadLobbyScreen() {
        final Game game = api.getSession().constructGame(priorState, n -> {
            loadingScreen.updateText("Loaded " + n + " players...");
        });

        if (game == null) {
            return;
        }

        api.setGame(game);

        lobbyScreen.load(api, gui);
    }

    private void loadLiveScreen() {
        final Game game = api.getSession().constructGame(priorState, n -> {
            loadingScreen.updateText("Loaded " + n + " players...");
        });

        if (game == null) {
            return;
        }

        api.setGame(game);

        liveScreen.load(api, gui);
    }
}
