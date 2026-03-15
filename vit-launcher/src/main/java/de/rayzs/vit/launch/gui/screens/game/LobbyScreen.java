package de.rayzs.vit.launch.gui.screens.game;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.launch.gui.screens.Screen;
import de.rayzs.vit.launch.gui.screens.game.elements.banners.LobbyPlayerBanner;
import de.rayzs.vit.launch.gui.screens.game.elements.window.LobbyPlayerWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class LobbyScreen extends Screen implements ActiveScreen {

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
                api.getGame().map().mapId()
        );


        final JPanel playersPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        for (Player player : api.getGame().players()) {
            final LobbyPlayerBanner playerBanner = new LobbyPlayerBanner(api, game, player, this);

            playerWindows.put(player.id(), new LobbyPlayerWindow(player));
            playerBanners.put(player.id(), playerBanner);
            playersPanel.add(playerBanner.getBanner());
        }


        contentPane.add(topLayerPanel, BorderLayout.NORTH);
        contentPane.add(playersPanel, BorderLayout.CENTER);
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
     * @param mapImageId Id of the map being played on.
     *
     * @return Created top layer.
     */
    private JPanel createTopLayer(
            final VITAPI api,
            final Game game,
            final String mapImageId
    ) {

        // Top map image. Will be behind the control boxes
        // like reload and weapon selection.
        final Image mapImage = ImageUtils.darkenImage(ImageUtils.cropImage(api
                        .getImageProvider()
                        .getMaps()
                        .getImage(mapImageId)
                        .getImage(),
                1000,
                200
        ), 0.58f);



        final JPanel banner = new JPanel() {
            @Override
            public void paintComponent(final Graphics graphics) {
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