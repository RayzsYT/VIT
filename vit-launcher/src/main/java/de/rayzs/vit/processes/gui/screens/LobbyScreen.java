package de.rayzs.vit.processes.gui.screens;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.gui.elements.BeautifiedToolTip;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.processes.gui.PlayerWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LobbyScreen extends Screen {


    private final String title = "v%s [%s]";
    private final String playerStats = "WR: %s%% | HS: %s%%";

    private final String roleDisplay = String.join("", new String[] {
            "<html><div style='",
            "color: rgba(%d, %d, %d, 1); ",
            "font-size: 60px;'><b>",
            "%s",
            "</b></div></html>"
    });

    private final String playerNameDisplay = String.join("", new String[] {
            "<html><div style='color: rgba(%d, %d, %d, 1)",
            "; font-size: 15px;'><b>%s</b></div></html>"
    });


    // Map of all players and their player windows.
    private final Map<Player, PlayerWindow> playerWindows = new HashMap<>();


    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        playerWindows.clear(); // clears up player windows in case it has been reloaded.
        gui.reset();


        final Game game = api.getGame();

        gui.setTitle(title.formatted(
                api.getVersion(),
                game.server()
        ));


        final JPanel contentPane = gui.getContentPane();


        final JPanel topLayerPanel = createTopLayer(
                api,
                game,
                api.getGame().mapId()
        );


        final JPanel playersPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        for (Player player : api.getGame().players()) {
            playersPanel.add(createPlayerBanner(api, game, player));
        }


        contentPane.add(topLayerPanel, BorderLayout.NORTH);
        contentPane.add(playersPanel, BorderLayout.CENTER);
        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }


    /**
     * Create JPanel for a player banner
     * showing information of a player.
     *
     * @param api VITAPI.
     * @param game Game.
     * @param player Player.
     * @return Created player banner.
     */
    private JPanel createPlayerBanner(
            final VITAPI api,
            final Game game,
            final Player player
    ) {

        // Create player window and map it with the player.
        playerWindows.put(player, new PlayerWindow(player));

        final JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics graphics) {
                final Graphics2D graphics2d = (Graphics2D) graphics;
                graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2d.setColor(GUI.Colors.BANNER_BACKGROUND.get());
                graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };


        // Open Player window on click
        banner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                playerWindows.get(player).show(event.getXOnScreen(), event.getYOnScreen());
            }
        });


        banner.setOpaque(false);

        banner.setPreferredSize(new Dimension(450, 170));
        banner.setMinimumSize(new Dimension(450, 170));

        banner.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


        final Color teamColor = switch (player.team()) {
            case ATTACK -> GUI.Colors.BANNER_ATTACKER.get();
            case DEFEND -> GUI.Colors.BANNER_DEFENDER.get();
        };

        final JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(4, 0));
        stripe.setBackground(teamColor);
        banner.add(stripe, BorderLayout.WEST);


        final JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(GUI.Colors.BANNER_BACKGROUND.get());
        inner.setBorder(BorderFactory.createEmptyBorder(2, 15, 0, 15));


        final JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);


        final JLabel nameLabel = new JLabel(formatPlayerName(game, player));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        nameLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        center.add(nameLabel);

        center.add(Box.createVerticalStrut(6));

        final JLabel statsLabel = new JLabel(playerStats.formatted("/", "/"));
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        statsLabel.setForeground(GUI.Colors.STATS_TEXT_FOREGROUND.get());
        center.add(statsLabel);


        center.add(Box.createVerticalGlue());
        inner.add(center, BorderLayout.CENTER);


        final JPanel rankPanel = new JPanel() {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };

        rankPanel.setOpaque(false);
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.X_AXIS));


        // Get current tiers. If one of them does not exist,
        // it will be simply ignored.

        final boolean hasCompetitive = player.competitive() != null;

        final Tier currentTier = hasCompetitive
                ? player.competitive().currentTier()
                : Tier.UNRANKED;

        final Tier peakTier = hasCompetitive
                ? player.competitive().seasonTiers().getPeakTier()
                : Tier.UNRANKED;



        final ImageIcon currentRankImage = currentTier.getImage()
                .getIcon(70, 70, Image.SCALE_SMOOTH);

        final JLabel currentRankLabel = new JLabel(currentRankImage) {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };


        currentRankLabel.setToolTipText("Current Rank: " + currentTier.getTierName());

        if (peakTier != Tier.UNRANKED) {
            final ImageIcon peakRankImage = peakTier.getImage()
                    .getIcon(38, 38, Image.SCALE_SMOOTH);

            final JLabel peakRankLabel = new JLabel(peakRankImage) {
                @Override
                public JToolTip createToolTip() {
                    return new BeautifiedToolTip(this);
                }
            };

            peakRankLabel.setToolTipText("Peak Rank: " + peakTier.getTierName());

            rankPanel.add(peakRankLabel);
            rankPanel.add(Box.createHorizontalStrut(8));
        }


        rankPanel.add(currentRankLabel);



        final JPanel rankWrapper = new JPanel(new GridBagLayout());
        rankWrapper.setOpaque(false);
        rankWrapper.add(rankPanel);

        inner.add(rankWrapper, BorderLayout.EAST);


        banner.add(inner, BorderLayout.CENTER);

        return banner;
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
     * Format the player name.
     *
     * @param game Game.
     * @param player Player.
     * @return Formatted player name.
     */
    private String formatPlayerName(final Game game, final Player player) {
        final Team team = player.team();
        final String playerName = player.name();

        // In case the player being formatted is the
        // user of the program.
        if (playerName.equalsIgnoreCase(game.self().name())) {
            return playerNameDisplay.formatted(250, 255, 181, "You");
        }

        // Formating to the corresponding color, depending
        // on what team the player is on.
        return switch (team) {
            case ATTACK -> playerNameDisplay.formatted(255, 99, 71, player.name());
            case DEFEND -> playerNameDisplay.formatted(0, 255, 255, player.name());
        };
    }
}