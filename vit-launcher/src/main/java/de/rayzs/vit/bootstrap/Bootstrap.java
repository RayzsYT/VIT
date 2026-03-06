package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.processes.loop.LoopHandler;
import de.rayzs.vit.processes.prepare.AssetPreparer;

import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("Starting VIT...");

        try {
            OutputLogger.initialize();

            start();
        } catch (Exception exception) {
            exception.printStackTrace();

            System.out.println("\n\nSomething went wrong! Program will terminate.");
            System.exit(0);
        }

    }

    private static void start() {
        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);


        final AssetPreparer prep = new AssetPreparer(api);


        final MainGUI gui = new MainGUI("Initializing...");
        gui.setVisible(true);

        final LoopHandler loop = new LoopHandler(api, gui);

        TimerTask task = new TimerTask() {
            public void run() {
                loop.handle();
            }
        };

        new Timer().scheduleAtFixedRate(task, 0, 2500);
    }
}
