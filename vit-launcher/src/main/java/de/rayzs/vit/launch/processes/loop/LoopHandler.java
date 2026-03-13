package de.rayzs.vit.launch.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.events.game.match.GameMatchEndEvent;
import de.rayzs.vit.api.event.events.game.match.GameMatchStartEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchDodgedEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchStartEvent;
import de.rayzs.vit.api.event.events.system.state.StateChangeEvent;
import de.rayzs.vit.api.event.events.gui.UpdateMainGuiEvent;
import de.rayzs.vit.api.event.events.system.tick.PreTickEvent;
import de.rayzs.vit.api.event.events.system.tick.TickEvent;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.launch.gui.screens.*;

public class LoopHandler {

    private final VITAPI api;
    private final MainGUI gui;


    private final InactiveScreen inactiveScreen = new InactiveScreen();
    private final LoadingScreen loadingScreen = new LoadingScreen();
    private final LobbyScreen lobbyScreen = new LobbyScreen();
    private final LiveScreen liveScreen = new LiveScreen();

    private final boolean hasGui;

    private SessionState priorState;


    public LoopHandler(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.gui = gui;
        this.hasGui = gui != null;

        if (hasGui) {
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


        final PreTickEvent preTickEvent = api.getEventManager().call(new PreTickEvent());

        if (preTickEvent.isCancelled()) {
            return;
        }


        final TickEvent tickEvent = api.getEventManager().call(new TickEvent(api.getSession().getSessionState()));
        final SessionState state = tickEvent.getState();


        if (!state.isValorantStarted()) {
            Request.unsetAuthToken();
            Request.unsetHeaders();
        }


        if (priorState == state) {
            return;
        }


        final StateChangeEvent stateChangeEvent = api.getEventManager().call(
                new StateChangeEvent(priorState, state)
        );


        if (hasGui) {
            final UpdateMainGuiEvent updateMainGuiEvent = api.getEventManager().call(
                    new UpdateMainGuiEvent(stateChangeEvent.getState(), gui)
            );

            if (updateMainGuiEvent.isCancelled()) {
                return;
            }

            loadingScreen.resetText();
        }


        switch (state) {

            case VALORANT_NOT_OPEN -> {
                if (!hasGui) return;

                inactiveScreen.load(api, gui);
            }

            case IN_MENU -> {
                if (!hasGui) return;

                loadingScreen.load(api, gui);
            }

            case IN_LOBBY -> {
                loadGameScreen(state, lobbyScreen);
            }

            case IN_GAME -> {
                loadGameScreen(state, liveScreen);
            }
        }


        // Event calls:
        if (priorState == SessionState.IN_MENU && state == SessionState.IN_LOBBY) {
            api.getEventManager().call(new GamePreMatchStartEvent(api.getGame()));   // Agent selection


        } else if (priorState == SessionState.IN_LOBBY && state == SessionState.IN_LOBBY) {
            api.getEventManager().call(new GamePreMatchDodgedEvent(api.getGame()));  // Dodge


        } else if (state == SessionState.IN_GAME) {
            api.getEventManager().call(new GameMatchStartEvent(api.getGame()));      // Match started


        } else if (priorState == SessionState.IN_GAME && state == SessionState.IN_MENU) {
            api.getEventManager().call(new GameMatchEndEvent(api.getGame()));        // Match ended

            Game.saveMatch(api.getGame()); // Save match into a file
        }


        priorState = state;
    }

    private void loadGameScreen(final SessionState state, final Screen displayScreen) {
        if (hasGui) loadingScreen.load(api, gui);

        final Game game = api.getSession().constructGame(state, map -> {
            gui.setTitle("Map: " + map.mapName());
        }, n -> {
            final String updateText = "Loaded " + n + " players...";
            System.out.println(updateText);
            if (hasGui) loadingScreen.updateText(updateText);
        });

        if (game == null) {
            return;
        }

        api.setGame(game);

        if (hasGui) displayScreen.load(api, gui);
    }
}
