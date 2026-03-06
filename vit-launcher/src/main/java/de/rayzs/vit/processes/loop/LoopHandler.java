package de.rayzs.vit.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.MainGUI;
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
    }

    public void handle() {

        if (!state.isInactive() && !this.api.getSession().isOpen()) {
            state = State.INACTIVE;

            Request.unsetAuthToken();
            Request.unsetHeaders();

            inactiveScreen.load(this.api, this.gui);
            return;
        }

        if (state.isInactive() && this.api.getSession().isOpen()) {
            state = State.WAITING;

            loadingScreen.resetText();
            api.getSession().initialize();

            loadingScreen.load(this.api, this.gui);
            return;
        }


        final boolean insideMatch = this.api.getSession().insideMatch();
        final boolean preparing = state.isLive() || state.isLoading();

        if (!preparing && insideMatch) {
            state = State.LOADING;

            loadLiveScreen();
            return;
        }


        if (preparing && !insideMatch) {
            state = State.WAITING;

            loadingScreen.resetText();
            loadingScreen.load(this.api, this.gui);
        }
    }

    private void loadLiveScreen() {
        // Create game object...
    }
}
