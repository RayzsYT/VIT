package de.rayzs.vit.launch.processes.gui.screens;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.gui.elements.BeautifiedButton;
import de.rayzs.vit.api.gui.elements.BeautifiedToolTip;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.launch.processes.gui.PlayerWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveScreen extends Screen {

    private final String title = "v%s [%s]";
    private final String playerStats = "RR: %d | WR: %.2f%% | HS: %.2f%%";

    private final String playerNameDisplay = String.join("", new String[]{
            "<html><div style='color: rgba(%d, %d, %d, 1)",
            "; font-size: 13px;'><b>%s</b></div></html>"
    });


    // Map of all players and their player windows.
    private final Map<Player, PlayerWindow> playerWindows = new HashMap<>();


    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        gui.reset();


        final Game game = api.getGame();

        gui.setTitle(title.formatted(api.getVersion(), game.server()));

        final JPanel contentPane = gui.getContentPane();
        final JPanel topLayerPanel = createTopLayer(api, gui, game.mapId());

        final JPanel playersPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());

        for (Player player : game.players()) {
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
    private JPanel createPlayerBanner(final VITAPI api, final Game game, final Player player) {

        // Create player window and map it with the player.
        playerWindows.put(player, new PlayerWindow(player));


        final JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics graphics) {
                final Graphics2D g = (Graphics2D) graphics;

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(GUI.Colors.BANNER_BACKGROUND.get());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };


        // Set cursor when entering banner panel
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));


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

        final JLabel agentImage = new JLabel(player.agent().getImage().getIcon(70, 70, Image.SCALE_SMOOTH));
        agentImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        final JPanel agentWrapper = new JPanel(new BorderLayout());
        agentWrapper.setOpaque(false);
        agentWrapper.add(agentImage, BorderLayout.SOUTH);
        inner.add(agentWrapper, BorderLayout.WEST);

        final JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        final JLabel nameLabel = new JLabel(formatPlayerName(game, player));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        nameLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        center.add(nameLabel);

        center.add(Box.createVerticalStrut(6));

        final JLabel statsLabel = new JLabel(playerStats.formatted(
                player.competitive() != null ? player.competitive().rr() : 0,
                player.stats().winRate(),
                player.stats().headShotRate()
        ));

        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        statsLabel.setForeground(GUI.Colors.STATS_TEXT_FOREGROUND.get());
        center.add(statsLabel);

        center.add(Box.createVerticalGlue());

        final Image weaponImage = ImageUtils.rescale(
                player.inventory().getWeaponSkin(api.getSelectedWeapon()).getImage(),
                140,
                40
        );

        final JLabel weaponLabel = new JLabel(new ImageIcon(weaponImage)) {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };

        weaponLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        weaponLabel.setToolTipText(player.inventory().getWeaponSkinName(api.getSelectedWeapon()));

        center.add(weaponLabel);
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

        final boolean hasCompetitive =
                player.competitive() != null &&
                        player.competitive().seasonTiers() != null;

        final Tier currentTier = hasCompetitive
                ? player.competitive().currentTier()
                : Tier.UNRANKED;

        final Tier peakTier = hasCompetitive
                ? player.competitive().seasonTiers().getPeakTier()
                : Tier.UNRANKED;

        final ImageIcon currentRankImage =
                currentTier.getImage().getIcon(70, 70, Image.SCALE_SMOOTH);

        final JLabel currentRankLabel = new JLabel(currentRankImage) {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };

        currentRankLabel.setToolTipText("Current Rank: " + currentTier.getTierName());

        if (peakTier != Tier.UNRANKED) {
            final ImageIcon peakRankImage =
                    peakTier.getImage().getIcon(38, 38, Image.SCALE_SMOOTH);

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
     * @param gui GUI. Required for reloading.
     * @param mapImageId Id of the map being played on.
     *
     * @return Created top layer.
     */
    private JPanel createTopLayer(final VITAPI api, final MainGUI gui, final String mapImageId) {

        final JPanel banner = new JPanel() {

            // Top map image. Will be behind the control boxes
            // like reload and weapon selection.
            private final Image mapImage = ImageUtils.cropImage(
                    api.getImageProvider().getMaps().getImage(mapImageId).getImage(),
                    1000,
                    200
            );

            @Override
            public void paintComponent(final Graphics graphics) {
                super.paintComponent(graphics);
                graphics.drawImage(mapImage, 0, 0, null);
            }
        };

        banner.setPreferredSize(new Dimension(1000, 200));
        banner.setLayout(null);

        // Creating control panel which includes the reload
        // and weapon selection boxes.
        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setBounds(20, 20, 350, 40);
        controls.setOpaque(false);

        final JButton refreshButton = new BeautifiedButton(
                "Refresh",
                GUI.Colors.CONTROL_BUTTON_BACKGROUND,
                GUI.Colors.TEXT_FOREGROUND,
                GUI.Colors.CONTROL_BUTTON_BACKGROUND_HOVER,
                GUI.Colors.CONTROL_BUTTON_BACKGROUND_PRESSED,
                GUI.Colors.CONTROL_BUTTON_BACKGROUND_RELEASED
        );

        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        refreshButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        refreshButton.addActionListener(e -> reload(api, gui));

        // Preparing weapon selection items, by first adding the currently
        // selected weapon and then the rest. Otherwise, it will feel off.
        // Trust me. I'm a professional UX designer #nofake
        final List<String> weaponSelectionOptions = new ArrayList<>();
        weaponSelectionOptions.add(api.getSelectedWeapon().getWeaponName());

        for (Weapon weapon : Weapon.values()) {
            if (!weaponSelectionOptions.contains(weapon.getWeaponName())) {
                weaponSelectionOptions.add(weapon.getWeaponName());
            }
        }

        final JComboBox<String> weaponSelector = new JComboBox<>(weaponSelectionOptions.toArray(new String[0]));

        weaponSelector.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        weaponSelector.setForeground(Color.WHITE);
        weaponSelector.setBackground(GUI.Colors.BANNER_ATTACKER.get());
        weaponSelector.setFocusable(false);
        weaponSelector.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        weaponSelector.addActionListener(e -> {
            final Weapon selectedWeapon =
                    Weapon.getWeaponByName((String) weaponSelector.getSelectedItem());

            api.setSelectedWeapon(selectedWeapon);
            reload(api, gui);
        });

        controls.add(refreshButton);
        controls.add(weaponSelector);

        banner.add(controls);

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
            case ATTACK -> playerNameDisplay.formatted(255, 99, 71, playerName);
            case DEFEND -> playerNameDisplay.formatted(0, 255, 255, playerName);
        };
    }

    /**
     * Reloads the GUI.
     *
     * @param api VITAPI.
     * @param gui MainGUI.
     */
    private void reload(final VITAPI api, final MainGUI gui) {
        new Thread(() ->
                SwingUtilities.invokeLater(() -> {

                    // clears up player windows in case it has been reloaded.
                    playerWindows.values().forEach(Window::dispose);
                    playerWindows.clear();

                    // Reload gui.
                    load(api, gui);
                    gui.revalidate();
                    gui.repaint();
                })
        ).start();
    }
}