package de.rayzs.vit.processes.screen;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.game.items.Agent;
import de.rayzs.vit.api.game.items.Team;
import de.rayzs.vit.api.game.items.Tier;
import de.rayzs.vit.api.game.items.Weapon;
import de.rayzs.vit.api.game.player.Player;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.gui.elements.BeautifiedToolTip;
import de.rayzs.vit.api.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LiveScreen extends Screen {


    private final String playerStats = "WR: %s%% | HS: %s%%";

    private final String playerNameDisplay = String.join("", new String[] {
            "<html><div style='color: rgba(%d, %d, %d, 1)",
            "; font-size: 13px;'><b>%s</b></div></html>"
    });


    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        gui.reset();
        gui.setTitle("In Game");


        final JPanel contentPane = gui.getContentPane();


        final JPanel topLayerPanel = createTopLayer(
                api,
                "2bee0dc9-4ffe-519b-1cbd-7fbe763a6047" // Placeholder map id
        );


        final JPanel playersPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        // Placeholder players
        boolean b = false;
        for (int i = 0; i < 10; i++) {
            b = !b;

            final Player player = Player.createPlayer(
                    "Player#" + i,
                    i,
                    "CardId-" + i,
                    "TitleId-" + i,
                    b ? Team.DEFEND : Team.ATTACK,
                    Agent.PHOENIX,
                    Tier.BRONZE_III,
                    Tier.IMMORTAL_II
            );

            playersPanel.add(createPlayerBanner(api, player));
        }


        contentPane.add(topLayerPanel, BorderLayout.NORTH);
        contentPane.add(playersPanel, BorderLayout.CENTER);
        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }


    /**
     * Create JPanel for a player banner
     * showing information of a player.
     *
     * @param player Player.
     * @return Created player banner.
     */
    private JPanel createPlayerBanner(final VITAPI api, final Player player) {

        final JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics graphics) {
                final Graphics2D graphics2d = (Graphics2D) graphics;
                graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2d.setColor(GUI.Colors.BANNER_BACKGROUND.get());
                graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
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


        final JLabel agentImage = new JLabel(
                player.agent().getImage().getIcon(90, 90, Image.SCALE_SMOOTH)
        );
        agentImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));


        final JPanel agentWrapper = new JPanel(new BorderLayout());
        agentWrapper.setOpaque(false);
        agentWrapper.add(agentImage, BorderLayout.SOUTH);
        inner.add(agentWrapper, BorderLayout.WEST);


        final JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);


        final JLabel nameLabel = new JLabel(formatPlayerName(player));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        nameLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        center.add(nameLabel);

        center.add(Box.createVerticalStrut(6));

        final JLabel statsLabel = new JLabel(playerStats.formatted("/", "/"));
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        statsLabel.setForeground(GUI.Colors.STATS_TEXT_FOREGROUND.get());
        center.add(statsLabel);


        center.add(Box.createVerticalGlue());

        final ImageIcon weaponImage = api
                .getImageProvider()
                .getWeaponSkins()
                .getImage("000ad7b1-44b0-9345-ea47-9cbd7dcdbb38") // Placeholder weapon
                .resizeIcon(0.3f);


        final JLabel weaponLabel = new JLabel(weaponImage) {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };

        weaponLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        weaponLabel.setToolTipText(api
                .getImageProvider()
                .getWeaponSkins()
                .getName("000ad7b1-44b0-9345-ea47-9cbd7dcdbb38")
        );

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

        final ImageIcon currentRankImage = api
                .getImageProvider()
                .getTiers()
                .getImage(player.currentTier().getTierId())
                .getIcon(70, 70, Image.SCALE_SMOOTH);

        final JLabel currentRankLabel = new JLabel(currentRankImage) {
            @Override
            public JToolTip createToolTip() {
                return new BeautifiedToolTip(this);
            }
        };

        currentRankLabel.setToolTipText("Current Rank: " + player.currentTier().getTierName());

        if (player.peakTier() != null) {
            final ImageIcon peakRankImage = api
                    .getImageProvider()
                    .getTiers()
                    .getImage(player.peakTier().getTierId())
                    .getIcon(38, 38, Image.SCALE_SMOOTH);

            final JLabel peakRankLabel = new JLabel(peakRankImage) {
                @Override
                public JToolTip createToolTip() {
                    return new BeautifiedToolTip(this);
                }
            };

            peakRankLabel.setToolTipText("Peak Rank: " + player.peakTier().getTierName());

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
     * @param mapImageId Id of the map being played on.
     *
     * @return Created top layer.
     */
    private JPanel createTopLayer(final VITAPI api, final String mapImageId) {

        // Top map image. Will be behind the control boxes
        // like reload and weapon selection.
        final Image mapImage = ImageUtils.cropImage(api
                        .getImageProvider()
                        .getMaps()
                        .getImage(mapImageId)
                        .getImage(),
                1000,
                200);

        final JPanel banner = new JPanel() {
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


        final JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        refreshButton.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        refreshButton.setBackground(GUI.Colors.BANNER_ATTACKER.get());
        refreshButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        refreshButton.addActionListener(e ->
                System.out.println("Refresh button clicked")
        );


        final JComboBox<String> weaponSelector = new JComboBox<>(
                Arrays.stream(Weapon.values())
                        .map(Weapon::getWeaponName)
                        .toArray(String[]::new)
        );

        weaponSelector.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        weaponSelector.setForeground(Color.WHITE);
        weaponSelector.setBackground(GUI.Colors.BANNER_ATTACKER.get());
        weaponSelector.setFocusable(false);
        weaponSelector.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        weaponSelector.addActionListener(e ->
                System.out.println("Selected: " + weaponSelector.getSelectedItem())
        );

        controls.add(refreshButton);
        controls.add(weaponSelector);

        banner.add(controls);

        return banner;
    }


    /**
     * Format the player name.
     *
     * @param player Player.
     * @return Formatted player name.
     */
    private String formatPlayerName(final Player player) {
        final Team team = player.team();
        final String playerName = player.name();

        // In case the player being formatted is the
        // user of the program.
        if (playerName.equalsIgnoreCase("missingname")) {
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