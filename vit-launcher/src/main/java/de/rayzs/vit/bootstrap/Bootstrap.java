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

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);


        final AssetPreparer prep = new AssetPreparer(api);


        final MainGUI gui = new MainGUI("Waiting");
        gui.setVisible(true);

        final LoopHandler loop = new LoopHandler(api, gui);

        TimerTask task = new TimerTask() {
            public void run() {
                loop.handle();
            }
        };

        new Timer().scheduleAtFixedRate(task, 0, 1000);
    }
}
