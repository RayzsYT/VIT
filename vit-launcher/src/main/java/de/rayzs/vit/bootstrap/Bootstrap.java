package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.event.events.gui.InitializeMainGuiEvent;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.gui.Screen;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.launch.guis.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.launch.ImplVITAPI;
import de.rayzs.vit.launch.processes.loop.LoopHandler;
import de.rayzs.vit.launch.processes.prepare.AssetPreparer;
import de.rayzs.vit.launch.screens.game.LiveScreen;
import de.rayzs.vit.launch.screens.game.LobbyScreen;
import de.rayzs.vit.launch.screens.ScreenAbstr;
import de.rayzs.vit.launch.processes.prepare.UpdateChecker;
import de.rayzs.vit.launch.screens.other.InactiveScreen;
import de.rayzs.vit.launch.screens.other.LoadingScreen;

import java.awt.image.RasterOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    public static void main(String[] args) {

        // Check if system is windows or not. Since VALORANT is for Windows only,
        // it only makes sense to focus on making VIT Windows only.
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.out.println("VIT only supports Windows!");
            return;
        }


        // Initializing
        System.out.println("Initializing logger...");

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

        System.out.println("Starting VIT...");


        final ImplVITAPI api = new ImplVITAPI();
        VIT.set(api);


        // Check if VIT is running on the latest version.
        // It then asks if the user want to update. If so,
        // then the rest of the code simply won't proceed.
        if (new UpdateChecker().wantToUpdate()) {
            return;
        }


        // Loading assets
        final AssetPreparer prep = new AssetPreparer(api);


        // Loading addons
        final long start = System.currentTimeMillis();
        api.getAddonManager().loadAddons();


        final int loadedAddons = VIT.get().getAddonManager().getLoadedAddons().size();

        System.out.println("Loaded "
                + loadedAddons
                + " addons in "
                + (System.currentTimeMillis() - start) + "ms!"
        );


        // First event call
        final InitializeMainGuiEvent initializeMainGuiEvent = api.getEventManager().call(
                new InitializeMainGuiEvent(new MainGUI("Initializing..."))
        );


        final MainGUI gui = initializeMainGuiEvent.isCancelled()
                ? null
                : (MainGUI) initializeMainGuiEvent.getGui();


        // Test screen
        if (args.length >= 1) {

            final int testIndex = StringUtils.searchIndex("--test=", args[0]);
            final int loadIndex = StringUtils.searchIndex("--load=", args[0]);


            // Load a match from a file.
            if (loadIndex != -1) {
                final String fileName = args[0].substring(loadIndex);
                final File file = FileDir.GAMES.getFile(fileName);

                if (!file.isFile() || !file.exists()) {
                    throw new NullPointerException("File does not exist! (" + fileName + ")");
                }

                final Game game = Game.loadMatch(file);
                api.setGame(game);

                if (gui != null) {
                    new LiveScreen().load(api, gui);
                    gui.setVisible(true);
                } else System.err.println("GUI is currently disabled!");

                return;
            }


            if (testIndex != -1) {

                int num = 12;
                if (args.length > 1) {

                    final int numIndex = StringUtils.searchIndex("--num=", args[1]);
                    if (numIndex != -1) {
                        num = Integer.parseInt(args[1].substring(numIndex));
                    }
                }

                final String name = args[0].substring(testIndex).toLowerCase(Locale.ROOT);
                final ScreenAbstr screen;

                switch (name) {
                    case "live" -> {
                        api.updateSessionState(SessionState.IN_GAME);
                        screen = new LiveScreen();
                    }
                    case "lobby" -> {
                        api.updateSessionState(SessionState.IN_LOBBY);
                        screen = new LobbyScreen();
                    }
                    case "loading" -> {
                        api.updateSessionState(SessionState.IN_LOBBY);
                        screen = new LoadingScreen();
                    }
                    case "inactive" -> {
                        api.updateSessionState(SessionState.VALORANT_NOT_OPEN);
                        screen = new InactiveScreen();
                    }

                    default -> throw new IllegalStateException("Invalid screen name! (" + name + ")");
                };

                TestDummy.apply(gui, screen, num, true);
            }

            return;
        }


        if (gui != null) {
            gui.setVisible(true);
        }


        final LoopHandler loop = new LoopHandler(api, gui);
        final TimerTask task = new TimerTask() {

            int tolerance = 0;
            long lastError = System.currentTimeMillis();

            public void run() {
                try {

                    loop.handle();

                } catch (Exception exception) {

                    if (System.currentTimeMillis() - lastError > 10000) {
                        lastError = System.currentTimeMillis();
                        tolerance = 0;
                    }

                    tolerance++;
                    exception.printStackTrace();

                    if (tolerance >= 2) {
                        System.err.println("Something fatal happened way too often! Program will terminate.");


                        OutputLogger.shutdown();


                        final File logFile = OutputLogger.getLogFile();

                        try {
                            Files.copy(
                                    logFile.toPath(),
                                    FileDir.CRASH_LOGS.getFile(logFile.getName()).toPath()
                            );
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                        System.exit(0);
                    }

                    System.err.println("Something fatal happened! Resetting the whole Live GUI hoping for auto recovery.");
                    loop.forceReset();

                }
            }
        };

        new Timer().scheduleAtFixedRate(task, 0, 100);
    }
}
