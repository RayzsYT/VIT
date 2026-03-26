package de.rayzs.vit.launch.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.events.game.match.GameMatchEndEvent;
import de.rayzs.vit.api.event.events.game.match.GameMatchStartEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchDodgedEvent;
import de.rayzs.vit.api.event.events.game.match.GamePreMatchStartEvent;
import de.rayzs.vit.api.event.events.system.state.StateChangeEvent;
import de.rayzs.vit.api.event.events.system.tick.PreTickEvent;
import de.rayzs.vit.api.event.events.system.tick.TickEvent;
import de.rayzs.vit.api.settings.Settings;
import de.rayzs.vit.launch.guis.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.api.request.Request;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopHandler {

    private final VITAPI api;
    private final MainGuiUpdater guiUpdater;


    private SessionState priorState = SessionState.VALORANT_NOT_OPEN;
    private long lastTick = System.currentTimeMillis();


    public LoopHandler(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.guiUpdater = new MainGuiUpdater(api, gui);

        this.guiUpdater.handle(priorState);
    }


    /**
     * Forces a reset. Basically disabled everything and completely
     * starts over from scratch. In case something fatal happened.
     */
    public void forceReset() {
        if (Request.isAuthTokenSet()) {
            Request.unsetAuthToken();
        }

        if (Request.areHeadersSet()) {
            Request.unsetHeaders();
        }


        priorState = SessionState.VALORANT_NOT_OPEN;
        guiUpdater.handle(priorState);
    }



    public void handle() {

        if (priorState != null) {
            final long time = System.currentTimeMillis() - lastTick;
            final long waiting = priorState.getWaitingInMillisTime();

            if (time < waiting) {
                return;
            }

            lastTick = System.currentTimeMillis();
        }


        if (!Request.areHeadersSet()) {
            try {
                api.getSession().initialize();
            } catch (Exception ignored) { return; }
        }


        final PreTickEvent preTickEvent = api.getEventManager().call(new PreTickEvent());

        if (preTickEvent.isCancelled()) {
            return;
        }


        final TickEvent tickEvent = api.getEventManager().call(
                new TickEvent( api.getSession().fetchSessionState() )
        );

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


        api.updateSessionState(state);
        guiUpdater.handle(state);


        if (state.isInsideMatch()) {
            loadGame(state);
        }


        // Event calls:
        if (priorState == SessionState.IN_MENU && state == SessionState.IN_LOBBY) {
            api.getEventManager().call(new GamePreMatchStartEvent(api.getGame()));   // Agent selection


        } else if (priorState == SessionState.IN_LOBBY && state == SessionState.IN_LOBBY) {
            api.getEventManager().call(new GamePreMatchDodgedEvent(api.getGame()));  // Dodge


        } else if (state == SessionState.IN_GAME) {
            api.getEventManager().call(new GameMatchStartEvent(api.getGame()));      // Match started


        } else if (priorState == SessionState.IN_GAME) {
            api.getEventManager().call(new GameMatchEndEvent(api.getGame()));        // Match ended

            // Save match as a file
            if (Settings.MATCH_ALWAYS_SAVE_AFTER.read()) {
                Game.saveMatch(api.getGame());
            }

            api.setGame(null);
        }


        priorState = state;
    }


    private void loadGame(
            final SessionState state
    ) {


        final AtomicBoolean rateLimited = new AtomicBoolean(false);


        final Game game = api.getSession().constructGame(state, event -> {
            final String server = event.getServer();
            final String map = event.getMap().mapName();

            guiUpdater.updateGUI(gui -> {
                gui.setTitle("VIT | Map: " + map + ", Server: " + server + " [Fetching players data]");
            });

        }, loadedPlayersCount -> {

            if (loadedPlayersCount == -1) {
                rateLimited.set(true);
                return;
            }


            final String updateText = "Loaded " + loadedPlayersCount + " players...";


            System.out.println(updateText);

            guiUpdater.updateLoadingScreen(loadingScreen -> {
                loadingScreen.updateText(updateText);
            });
        });


        if (game == null) {

            if (rateLimited.get()) {
                final AtomicInteger time = new AtomicInteger(0);
                final int waitingTime = 10;

                while (time.getAndIncrement() < waitingTime) {

                    guiUpdater.updateLoadingScreen(loadingScreen -> {
                        loadingScreen.updateText("Rate limited! (" + (waitingTime - time.get()) + "s)");
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) { }
                }
            }

            return;
        }

        api.setGame(game);
        guiUpdater.loadGameScreen(state);
    }
}
