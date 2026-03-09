package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.launch.VITAPIImpl;
import de.rayzs.vit.launch.processes.loop.LoopHandler;
import de.rayzs.vit.launch.processes.prepare.AssetPreparer;
import de.rayzs.vit.launch.processes.gui.screens.LiveScreen;
import de.rayzs.vit.launch.processes.gui.screens.LobbyScreen;
import de.rayzs.vit.launch.processes.gui.screens.Screen;
import de.rayzs.vit.launch.processes.prepare.UpdateChecker;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("Starting VIT...");

        try {
            OutputLogger.initialize();

            start(args);
        } catch (Exception exception) {
            exception.printStackTrace();

            System.out.println("\n\nSomething went wrong! Program will terminate.");
            System.exit(0);
        }

    }

    private static void start(final String[] args) {
        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);


        final AssetPreparer prep = new AssetPreparer(api);


        final MainGUI gui = new MainGUI("Initializing...");
        gui.setVisible(true);


        // Test screen
        if (args.length >= 1) {

            final int testIndex = StringUtils.searchIndex("--test=", args[0]);

            if (testIndex != -1) {

                int num = 12;
                if (args.length > 1) {

                    final int numIndex = StringUtils.searchIndex("--num=", args[1]);
                    if (numIndex != -1) {
                        num = Integer.parseInt(args[1].substring(numIndex));
                    }
                }

                final String name = args[0].substring(testIndex).toLowerCase(Locale.ROOT);
                final Screen screen = switch (name) {
                    case "live" -> new LiveScreen();
                    case "lobby" -> new LobbyScreen();
                    default -> throw new IllegalStateException("Invalid screen name! (" + name + ")");
                };

                TestDummy.apply(gui, screen, num);
            }

            return;
        }


        // Check if VIT is running on the latest version.
        // It then asks if the user want to update. If so,
        // then the rest of the code simply won't proceed.
        if (new UpdateChecker().wantToUpdate()) {
            return;
        }


        final LoopHandler loop = new LoopHandler(api, gui);

        TimerTask task = new TimerTask() {
            public void run() {
                loop.handle();
            }
        };

        new Timer().scheduleAtFixedRate(task, 0, 2500);
    }
}
