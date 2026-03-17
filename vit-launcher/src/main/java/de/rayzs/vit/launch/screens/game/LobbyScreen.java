package de.rayzs.vit.launch.screens.game;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.launch.screens.Screen;
import de.rayzs.vit.launch.screens.game.elements.banners.LobbyPlayerBanner;
import de.rayzs.vit.launch.screens.game.elements.window.LobbyPlayerWindow;
import de.rayzs.vit.launch.screens.game.elements.window.PlayerWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class LobbyScreen extends Screen implements GameScreen {

    private final String roleDisplay = String.join("", new String[] {
            "<html><div style='",
            "color: rgba(%d, %d, %d, 1); ",
            "font-size: 60px;'><b>",
            "%s",
            "</b></div></html>"
    });



    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        super.load(api, gui);

        final Game game = api.getGame();

        gui.setTitle(StringUtils.replace(TITLE,
                "%map%", game.map().mapName(),
                "%server%", game.server()
        ));


        final JPanel contentPane = gui.getContentPane();
        final JPanel topLayerPanel = createTopLayer(
                api,
                game,
                gui,
                api.getGame().map().mapId()
        );

        contentPane.add(topLayerPanel, BorderLayout.NORTH);


        final JPanel playersPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        final JPanel playersWaitingPanel = new JPanel();
        playersWaitingPanel.setBorder(BorderFactory.createEmptyBorder(100, 10, 10, 10));


        final JLabel playersWaitingLabel = new JLabel();

        playersWaitingLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        playersWaitingPanel.setBackground(GUI.Colors.BACKGROUND.get());

        playersWaitingLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
        playersWaitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playersWaitingLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        playersWaitingPanel.add(playersWaitingLabel);

        contentPane.add(playersWaitingPanel, BorderLayout.CENTER);


        new Thread(() -> {

            final Player[] players = game.players();
            final int max = players.length;

            playersWaitingLabel.setText(loadingPlayerBannersText.formatted(0, max));

            for (int i = 0; i < max; i++) {
                final Player player = players[i];
                final LobbyPlayerBanner playerBanner = new LobbyPlayerBanner(api, game, player, this);

                playerWindows.put(player.id(), new LobbyPlayerWindow(player));
                playerBanners.put(player.id(), playerBanner);

                playerBanner.getBanner().setVisible(false);
                playersPanel.add(playerBanner.getBanner());

                playersWaitingLabel.setText(loadingPlayerBannersText.formatted(i, max));
            }

            contentPane.remove(playersWaitingPanel);
            contentPane.add(playersPanel, BorderLayout.CENTER);
            playerBanners.values().forEach(banner -> banner.getBanner().setVisible(true));
        }).start();


        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }


    /**
     * Creates the top layer of the gui
     * which has an image of the map being played on as well
     * as the control boxes to either reload the gui or switch
     * the default weapon skin which is being shown.
     *
     * @param api VITAPI.
     * @param game Game.
     * @param gui GUI. Required for reloading.
     * @param mapImageId ID of the map being played on.
     *
     * @return Created top layer.
     */
    private JPanel createTopLayer(
            final VITAPI api,
            final Game game,
            final MainGUI gui,
            final String mapImageId
    ) {

        final JPanel banner = new JPanel() {

            @Override
            public void paintComponent(final Graphics graphics) {

                // Top map image. Will be behind the control boxes
                // like reload and weapon selection.
                final Image mapImage = ImageUtils.darkenImage(ImageUtils.cropImage(api
                                .getImageProvider()
                                .getMaps()
                                .getImage(mapImageId)
                                .getImage(),
                        gui.getWidth(),
                        200
                ), 0.58f);

                super.paintComponent(graphics);
                graphics.drawImage(mapImage, 0, 0, null);
            }
        };

        banner.setPreferredSize(new Dimension(1000, 200));
        banner.setLayout(new GridBagLayout());


        final Color roleColor = switch (game.self().team()) {
            case ATTACK -> GUI.Colors.LOBBY_ROLE_ATTACKING.get();
            case DEFEND -> GUI.Colors.LOBBY_ROLE_DEFENDING.get();
        };

        final JLabel roleInfo = new JLabel(roleDisplay.formatted(
                roleColor.getRed(),
                roleColor.getGreen(),
                roleColor.getBlue(),
                api.getGame().self().team().getTeamAdjective().toUpperCase(Locale.ROOT)
        ));

        roleInfo.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        roleInfo.setOpaque(false);

        banner.add(roleInfo);

        return banner;
    }


    /**
     * Clears entire cache.
     */
    @Override
    public void clearCache() {
        playerWindows.values().forEach(PlayerWindow::dispose);
        playerWindows.clear();
        playerBanners.clear();
    }


    /**
     * Open a player window at the exact location.
     *
     * @param player Player.
     * @param x X location.
     * @param y Y location.
     */
    @Override
    public void openPlayerWindow(
            final Player player,
            final int x,
            final int y
    ) {
        playerWindows.get(player.id()).show(x, y);
    }
}