package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.processes.prepare.AssetPreparer;
import de.rayzs.vit.processes.screen.InactiveScreen;
import de.rayzs.vit.processes.screen.LiveScreen;
import de.rayzs.vit.processes.screen.LoadingScreen;

import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    public static void main(String[] args) {

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);


        final AssetPreparer prep = new AssetPreparer(api);


        final MainGUI gui = new MainGUI("Waiting");
        gui.setVisible(true);

        final InactiveScreen inactiveScreen = new InactiveScreen();
        final LoadingScreen loadingScreen = new LoadingScreen();
        final LiveScreen liveScreen = new LiveScreen();
        inactiveScreen.load(api, gui);

        TimerTask task = new TimerTask() {
            int i = 1;

            public void run() {
                i = (++i % 3);

                switch (i) {
                    case 0: inactiveScreen.load(api, gui); break;
                    case 1: loadingScreen.load(api, gui); break;
                    case 2: liveScreen.load(api, gui); break;
                }
            }
        };

        new Timer().scheduleAtFixedRate(task, 5000, 3000);
    }
}
