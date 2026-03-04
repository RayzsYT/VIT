package de.rayzs.vit.processes.screen;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.game.items.Agent;
import de.rayzs.vit.api.game.items.Team;
import de.rayzs.vit.api.game.items.Weapon;
import de.rayzs.vit.api.game.player.Player;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.bootstrap.Bootstrap;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LiveScreen extends Screen {


    private final String playerStats = "WR: %s% | HS: %s%";


    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        gui.reset();
        gui.setTitle("In Game");


        final JPanel contentPane = gui.getContentPane();


        // Top map banner which indicates what map is being played on.
        final JPanel mapBannerPanel = new JPanel(new BorderLayout());
        mapBannerPanel.setPreferredSize(new Dimension(0, 180));
        mapBannerPanel.setBackground(Color.BLACK);

        final JLabel mapImage = new JLabel();
        mapImage.setHorizontalAlignment(SwingConstants.CENTER);
        mapImage.setIcon(api
                .getImageProvider()
                .getMaps()
                .getImage("2bee0dc9-4ffe-519b-1cbd-7fbe763a6047") // Placeholder map
                .getIcon()
        );

        mapBannerPanel.add(createTopControls(), BorderLayout.NORTH);
        mapBannerPanel.add(mapImage, BorderLayout.CENTER);
        mapBannerPanel.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, GUI.Colors.BANNER_BORDER.get()
        ));


        final JPanel playersPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        playersPanel.setBackground(GUI.Colors.BACKGROUND.get());


        // Placeholder players
        boolean b = false;
        for (int i = 0; i < 10; i++) {
            b = !b;

            final Player player = new Player(
                    "Player#" + i,
                    b ? Team.ATTACK : Team.DEFEND,
                    Agent.HARBOR,
                    i,
                    "CardId-" + i,
                    "TitleId-" + i
            );

            playersPanel.add(createPlayerBanner(player));
        }


        contentPane.add(mapBannerPanel, BorderLayout.NORTH);
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
    private JPanel createPlayerBanner(Player player) {

        final JPanel banner = new JPanel(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        banner.setBackground(GUI.Colors.BANNER_BACKGROUND.get());
        banner.setPreferredSize(new Dimension(450, 100));


        // Color stripe on the left side.
        final Color teamColor = switch (player.team()) {
            case ATTACK -> GUI.Colors.BANNER_ATTACKER.get();
            case DEFEND -> GUI.Colors.BANNER_DEFENDER.get();
        };


        // The stripe left side of the player name.
        final JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(6, 0));
        stripe.setBackground(teamColor);

        banner.add(stripe, BorderLayout.WEST);



        final JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(
                0, 12, 0, 0
        ));


        // Agent image
        final JLabel agentImage = new JLabel(
                player.agent().getImage().getIcon(70, 70, Image.SCALE_SMOOTH)
        );

        agentImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        innerPanel.add(agentImage, BorderLayout.WEST);



        final JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);


        final JLabel nameLabel = new JLabel(player.name());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        nameLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());

        center.add(nameLabel);
        center.add(Box.createVerticalStrut(5));


        // Player statistics in short summary.
        final JLabel statsLabel = new JLabel(playerStats);
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statsLabel.setForeground(GUI.Colors.TEXT_FOREGROUND.get());

        center.add(statsLabel);


        innerPanel.add(center, BorderLayout.CENTER);

        banner.add(innerPanel, BorderLayout.CENTER);

        return banner;
    }


    private JPanel createTopControls() {
        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controls.setBackground(GUI.Colors.BACKGROUND.get());


        final JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(GUI.Colors.BANNER_ATTACKER.get());
        refreshButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        refreshButton.addActionListener(e -> {
            System.out.println("Refresh button clicked");
        });



        final JComboBox<String> weaponSelector = new JComboBox<>(
                Arrays.stream(Weapon.values()).map(Weapon::getWeaponName).toArray(String[]::new)
        );

        weaponSelector.setSelectedItem(Weapon.ARES); // Placeholder by default selected weapon.

        weaponSelector.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        weaponSelector.setForeground(Color.WHITE);
        weaponSelector.setBackground(GUI.Colors.BANNER_ATTACKER.get());
        weaponSelector.setFocusable(false);

        weaponSelector.addActionListener(e -> {
            final String selected = (String) weaponSelector.getSelectedItem();
            System.out.println("Selected: " + selected);
        });

        controls.add(refreshButton);
        controls.add(weaponSelector);

        return controls;
    }
}