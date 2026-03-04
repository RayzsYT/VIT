package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.processes.screen.InactiveScreen;
import de.rayzs.vit.processes.screen.LoadingScreen;

import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    public static void main(String[] args) {

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);


        //final AssetPreparer prep = new AssetPreparer(api);


        final MainGUI gui = new MainGUI("Waiting");
        gui.setVisible(true);

        final InactiveScreen inactiveScreen = new InactiveScreen();
        final LoadingScreen loadingScreen = new LoadingScreen();
        inactiveScreen.load(api, gui);

        TimerTask task = new TimerTask() {
            boolean b = true;

            public void run() {
                b = !b;

                System.out.println(b);

                if (b) inactiveScreen.load(api, gui);
                else loadingScreen.load(api, gui);
            }
        };

        new Timer().scheduleAtFixedRate(task, 5000, 5000);
    }
}
