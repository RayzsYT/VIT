package de.rayzs.vit.processes.loop;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.processes.screen.InactiveScreen;
import de.rayzs.vit.processes.screen.LiveScreen;
import de.rayzs.vit.processes.screen.LoadingScreen;

public class LoopHandler {

    private final VITAPI api;
    private final MainGUI gui;


    private final InactiveScreen inactiveScreen = new InactiveScreen();
    private final LoadingScreen loadingScreen = new LoadingScreen();
    private final LiveScreen liveScreen = new LiveScreen();


    public LoopHandler(final VITAPI api, final MainGUI gui) {
        this.api = api;
        this.gui = gui;

        gui.setAlwaysOnTop(true);
        gui.setAlwaysOnTop(false);

        this.liveScreen.load(api, gui);
    }

    public void handle() {

    }
}
