package de.rayzs.vit.launch.processes.gui;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.elements.BeautifiedButton;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.utils.ImageUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class PlayerWindow extends GUI {


    private final String matchDisplay = String.join("", new String[] {
            "<html><center><div style='",
            "color: rgba(%d, %d, %d, 1); ",
            "font-size: 24px;'><b>",
            "%d  -  %s  -  %d",
            "</b></div><br><div style='",
            "color: rgba(255, 255, 255, 1); ",
            "font-size: 15px;'>",
            "RR: %d | HS: %.2f%%",
            "</div></center></html>"
    });


    private final Player player;
    private boolean swapView = false;

    public PlayerWindow(final Player player) {
        super(player.name(), 500, 600);

        this.player = player;

        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Colors.BACKGROUND.get());

        final JPanel banner = new JPanel() {

            private final Image mapImage = ImageUtils.darkenImage(
                    ImageUtils.cropImage(
                            VIT.get()
                                    .getImageProvider().getMaps()
                                    .getImage("1c18ab1f-420d-0d8b-71d0-77ad3c439115")
                                    .getImage(),
                            1000,
                            200
                    ), 0.6f
            );


            @Override
            protected void paintComponent(final Graphics graphics) {
                super.paintComponent(graphics);

                graphics.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
            }
        };

        banner.setPreferredSize(new Dimension(500, 120));
        banner.setLayout(new BorderLayout());

        final JPanel bannerInner = new JPanel(new BorderLayout());
        bannerInner.setOpaque(false);
        bannerInner.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        final JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        final ImageIcon agentImage = player.agent().getImage().getIcon(70, 70, Image.SCALE_SMOOTH);
        final JLabel agentLabel = new JLabel(agentImage);

        final JLabel nameLabel = new JLabel(shortenPlayerName(player));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        left.add(agentLabel);
        left.add(nameLabel);

        final JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);

        final JPanel buttonContainer = new JPanel(new GridLayout(1, 2, 10, 8));
        buttonContainer.setOpaque(false);

        final JButton skinsButton = new BeautifiedButton(
                "View Skins",
                GUI.Colors.PLAYER_BUTTON_BACKGROUND,
                GUI.Colors.TEXT_FOREGROUND,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_HOVER,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_PRESSED,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_RELEASED
        );

        final JButton historyButton = new BeautifiedButton(
                "View Match History",
                GUI.Colors.PLAYER_BUTTON_BACKGROUND,
                GUI.Colors.TEXT_FOREGROUND,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_HOVER,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_PRESSED,
                GUI.Colors.PLAYER_BUTTON_BACKGROUND_RELEASED
        );

        skinsButton.setEnabled(swapView);

        buttonContainer.add(skinsButton);
        buttonContainer.add(historyButton);

        right.add(buttonContainer);

        bannerInner.add(left, BorderLayout.SOUTH);
        bannerInner.add(right, BorderLayout.EAST);

        banner.add(bannerInner, BorderLayout.CENTER);

        final CardLayout cardLayout = new CardLayout();
        final JPanel body = new JPanel(cardLayout);
        body.setBackground(Colors.BACKGROUND.get());

        final JPanel skinsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        skinsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        skinsPanel.setBackground(Colors.BACKGROUND.get());

        for (final Weapon weapon : Weapon.values()) {
            skinsPanel.add(createWeaponBanner(weapon));
        }


        final JScrollPane skinsScroll = new JScrollPane(skinsPanel);
        skinsScroll.getVerticalScrollBar().setUnitIncrement(4);
        skinsScroll.getViewport().setBackground(Colors.BACKGROUND.get());
        skinsScroll.setBorder(null);

        skinsScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        skinsScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Colors.PLAYER_SCROLLBAR.get();
                this.trackColor = Colors.BACKGROUND.get();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                final JButton button = new JButton();

                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });


        final JPanel historyPanel;

        if (player.playedMatches().length == 0) {
            historyPanel = new JPanel(new GridBagLayout());
            historyPanel.setBackground(Colors.BACKGROUND.get());

            final JLabel emptyLabel = new JLabel("No matches found!");
            emptyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            emptyLabel.setForeground(Colors.TEXT_FOREGROUND.get());

            historyPanel.add(emptyLabel);
        } else {
            historyPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            historyPanel.setBackground(Colors.BACKGROUND.get());

            for (final Match match : player.playedMatches()) {
                historyPanel.add(createMatchBanner(match));
            }
        }

        final JScrollPane historyScroll = new JScrollPane(historyPanel);
        historyScroll.getVerticalScrollBar().setUnitIncrement(4);
        historyScroll.getViewport().setBackground(Colors.BACKGROUND.get());
        historyScroll.setBorder(null);

        historyScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        historyScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Colors.PLAYER_SCROLLBAR.get();
                this.trackColor = Colors.BACKGROUND.get();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                final JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        body.add(skinsScroll, "skins");
        body.add(historyScroll, "history");

        skinsButton.addActionListener(e -> {
            swapView = false;

            skinsButton.setEnabled(false);
            historyButton.setEnabled(true);

            cardLayout.show(body, "skins");
        });

        historyButton.addActionListener(e -> {
            swapView = true;

            skinsButton.setEnabled(true);
            historyButton.setEnabled(false);

            cardLayout.show(body, "history");
        });

        contentPane.add(banner, BorderLayout.NORTH);
        contentPane.add(body, BorderLayout.CENTER);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(contentPane);
    }




    private JPanel createWeaponBanner(final Weapon weapon) {

        final JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics graphics) {
                final Graphics2D graphics2D = (Graphics2D) graphics;

                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(Colors.BANNER_BACKGROUND.get());
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(450, 70));
        banner.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        final JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        final Image weaponImage = ImageUtils.rescale(
                player.inventory().getWeaponSkin(weapon).getImage(),
                120,
                35
        );

        final JLabel weaponLabel = new JLabel(new ImageIcon(weaponImage));
        weaponLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        final JLabel nameLabel = new JLabel(player.inventory().getWeaponSkinName(weapon));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        nameLabel.setForeground(Colors.TEXT_FOREGROUND.get());

        inner.add(weaponLabel, BorderLayout.WEST);
        inner.add(nameLabel, BorderLayout.EAST);

        banner.add(inner, BorderLayout.CENTER);

        return banner;
    }




    private JPanel createMatchBanner(final Match match) {
        final JPanel banner = new JPanel() {
            private final Image mapImage = ImageUtils.darkenImage(
                    ImageUtils.cropImage(
                            VIT.get()
                                    .getImageProvider().getMaps()
                                    .getImage(match.mapId()).getImage(),
                            1000,
                            200
                    ), 0.5f
            );


            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
            }
        };

        banner.setPreferredSize(new Dimension(450, 120));
        banner.setLayout(new GridBagLayout());


        final Color textColor = match.stats().won()
                ? Colors.PLAYER_MATCH_WON.get()
                : Colors.PLAYER_MATCH_LOST.get();

        final JLabel text = new JLabel(
                matchDisplay.formatted(
                        textColor.getRed(),
                        textColor.getGreen(),
                        textColor.getBlue(),
                        match.stats().wonRounds(),
                        match.stats().won() ? "WON" : "LOST",
                        match.stats().lostRounds(),

                        match.compMatchResult().rr(),
                        match.stats().headshotRate()
                )
        );

        banner.add(text);

        return banner;
    }



    /**
     * Show the player window at the mouse
     * coordination.
     *
     * @param x Mouse x location.
     * @param y Mouse y location.
     */
    public void show(final int x, final int y) {
        setLocation(x, y);
        setVisible(true);
    }

    /**
     * Shortens a player name to a constant size.
     * Used for names to guarantee that longs names
     * don't move the buttons.
     *
     * @param player Player.
     *
     * @return Shorten player name.
     */
    private String shortenPlayerName(final Player player) {
        final String name = player.name();
        final int max = 18;

        if (name.length() <= max)
            return name;

        return name.substring(0, max) + "...";
    }
}