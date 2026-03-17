package de.rayzs.vit.launch.screens.game;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.gui.elements.BeautifiedButton;
import de.rayzs.vit.api.gui.elements.BeautifiedJComboBox;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.launch.screens.game.elements.banners.LivePlayerBanner;
import de.rayzs.vit.launch.screens.game.elements.banners.PlayerBanner;
import de.rayzs.vit.launch.screens.Screen;
import de.rayzs.vit.launch.screens.game.elements.window.LivePlayerWindow;
import de.rayzs.vit.launch.screens.game.elements.window.PlayerWindow;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LiveScreen extends Screen implements GameScreen {


    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        super.load(api, gui);

        final Game game = api.getGame();

        gui.setTitle(StringUtils.replace(TITLE,
                "%map%", game.map().mapName(),
                "%server%", game.server()
        ));

        final JPanel contentPane = gui.getContentPane();
        final JPanel topLayerPanel = createTopLayer(api, gui, game.map().mapId());
        contentPane.add(topLayerPanel, BorderLayout.NORTH);

        final JPanel playersPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        final List<Player> team1Players = new ArrayList<>();
        final List<Player> team2Players = new ArrayList<>();

        final Player selfPlayer = game.self();

        team1Players.add(selfPlayer);
        for (final Player player : game.players()) {
            if (team1Players.contains(player)) {
                continue;
            }

            if (player.team() == selfPlayer.team()) {
                team1Players.add(player);
            } else {
                team2Players.add(player);
            }
        }


        final int minRequiredPlayerBanners = 5;
        final int max = Math.max(minRequiredPlayerBanners,
                Math.max(team1Players.size(), team2Players.size())
        );


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
            final int maxPlayers = game.players().length;
            int loaded = 0;

            playersWaitingLabel.setText(loadingPlayerBannersText.formatted(loaded, maxPlayers));

            for (int i = 0; i < max; i++) {
                final boolean shouldCreateEmptyBanner = game.players().length == 1
                        || !team2Players.isEmpty() && i < minRequiredPlayerBanners;

                if (i < team1Players.size()) {
                    final Player player = team1Players.get(i);
                    final PlayerBanner playerBanner = new LivePlayerBanner(
                            api, game, player, this
                    );

                    playerWindows.put(player.id(), new LivePlayerWindow(player));
                    playerBanners.put(player.id(), playerBanner);

                    playerBanner.getBanner().setVisible(false);
                    playersPanel.add(playerBanner.getBanner());

                    playersWaitingLabel.setText(loadingPlayerBannersText.formatted(++loaded, maxPlayers));
                } else if (shouldCreateEmptyBanner) {
                    playersPanel.add(createEmptyPlayerBanner());
                }

                if (i < team2Players.size()) {
                    final Player player = team2Players.get(i);
                    final PlayerBanner playerBanner = new LivePlayerBanner(
                            api, game, player, this
                    );

                    playerWindows.put(player.id(), new LivePlayerWindow(player));
                    playerBanners.put(player.id(), playerBanner);

                    playerBanner.getBanner().setVisible(false);
                    playersPanel.add(playerBanner.getBanner());

                    playersWaitingLabel.setText(loadingPlayerBannersText.formatted(++loaded, maxPlayers));
                } else if (shouldCreateEmptyBanner) {
                    playersPanel.add(createEmptyPlayerBanner());
                }

            }

            contentPane.remove(playersWaitingPanel);
            contentPane.add(playersPanel, BorderLayout.CENTER);
            playerBanners.values().forEach(banner -> banner.getBanner().setVisible(true));
        }).start();


        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }


    /**
     * Creates an empty player banner as a placeholder
     * to fill the grid.
     *
     * @return Empty player banner.
     */
    private JPanel createEmptyPlayerBanner() {
        final JPanel banner = new JPanel();

        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(450, 170));
        banner.setMinimumSize(new Dimension(450, 170));

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
     * @param mapImageId ID of the map being played on.
     *
     * @return Created top layer.
     */
    private JPanel createTopLayer(final VITAPI api, final MainGUI gui, final String mapImageId) {

        final JPanel banner = new JPanel() {

            @Override
            public void paintComponent(final Graphics graphics) {

                // Top map image. Will be behind the control boxes
                // like reload and weapon selection.
                final Image mapImage = ImageUtils.cropImage(
                        api.getImageProvider().getMaps().getImage(mapImageId).getImage(),
                        gui.getWidth(),
                        200
                );

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

        final JComboBox<String> weaponSelector = new BeautifiedJComboBox<>(
                weaponSelectionOptions.toArray(new String[0]),
                GUI.Colors.CONTROL_COMBOBOX_HOVER,
                GUI.Colors.TEXT_FOREGROUND,
                GUI.Colors.CONTROL_COMBOBOX_BACKGROUND,
                GUI.Colors.TEXT_FOREGROUND,
                GUI.Colors.CONTROL_COMBOBOX_ARROW,
                GUI.Colors.CONTROL_COMBOBOX_SCROLL_FOREGROUND,
                GUI.Colors.CONTROL_COMBOBOX_SCROLL_BACKGROUND
        );

        weaponSelector.addActionListener(e -> {
            final Weapon selectedWeapon =
                    Weapon.getWeaponByName((String) weaponSelector.getSelectedItem());

            api.setSelectedWeapon(selectedWeapon);


            // Updating player banners
            gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            for (final Player player : api.getGame().players()) {
                playerBanners.get(player.id()).updatePlayer(player);
            }

            gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        });

        controls.add(refreshButton);
        controls.add(weaponSelector);

        banner.add(controls);

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
     * Reloads the GUI.
     *
     * @param api VITAPI.
     * @param gui MainGUI.
     */
    private void reload(final VITAPI api, final MainGUI gui) {
        new Thread(() ->
                SwingUtilities.invokeLater(() -> {

                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    load(api, gui);
                    gui.revalidate();
                    gui.repaint();

                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        ).start();
    }
}