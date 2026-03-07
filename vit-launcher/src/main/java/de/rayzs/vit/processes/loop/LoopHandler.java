package de.rayzs.vit.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.session.SessionState;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.processes.screen.InactiveScreen;
import de.rayzs.vit.processes.screen.LiveScreen;
import de.rayzs.vit.processes.screen.LoadingScreen;

public class LoopHandler {

    private enum State {
        INACTIVE, RUNNING, WAITING, LOADING, LIVE;

        public boolean isInactive() {
            return this == INACTIVE;
        }

        public boolean isRunning() {
            return this == RUNNING;
        }

        public boolean isWaiting() {
            return this == WAITING;
        }

        public boolean isLoading() {
            return this == LOADING;
        }

        public boolean isLive() {
            return this == LIVE;
        }
    }


    private final VITAPI api;
    private final MainGUI gui;


    private final InactiveScreen inactiveScreen = new InactiveScreen();
    private final LoadingScreen loadingScreen = new LoadingScreen();
    private final LiveScreen liveScreen = new LiveScreen();

    private State state = State.INACTIVE;


    public LoopHandler(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.gui = gui;

        gui.setAlwaysOnTop(true);
        gui.setAlwaysOnTop(false);

        inactiveScreen.load(api, gui);
    }

    public void handle() {

        if (!Request.areHeadersSet()) {
            state = State.INACTIVE;

            System.out.println("Waiting 'til VALORANT is enabled");
            api.getSession().initialize();
            return;
        }


        final SessionState sessionState = api.getSession().getSessionState();


        if (!state.isInactive() && !sessionState.isValorantStarted()) {
            System.out.println("Set state to inactive");
            state = State.INACTIVE;

            Request.unsetHeaders();

            inactiveScreen.load(api, gui);
            return;
        }

        if (state.isInactive() && sessionState.isValorantStarted()) {
            System.out.println("Detected game. Set state to waiting");
            state = State.WAITING;

            loadingScreen.resetText();

            loadingScreen.load(this.api, this.gui);
            return;
        }


        final boolean preparing = state.isLive() || state.isLoading();

        if (!preparing && sessionState.isInsideMatch()) {
            System.out.println("Found live match");
            state = State.LOADING;

            loadLiveScreen();
            return;
        }


        if (preparing && !sessionState.isInsideMatch()) {
            state = State.WAITING;

            loadingScreen.resetText();
            loadingScreen.load(this.api, this.gui);
        }
    }

    private void loadLiveScreen() {
        final Game game = api.getSession().constructGame(n -> {
            loadingScreen.updateText("Loaded " + n + " players...");
        });

        if (game == null) {
            return;
        }

        api.setGame(game);

        liveScreen.load(api, gui);
    }
}
