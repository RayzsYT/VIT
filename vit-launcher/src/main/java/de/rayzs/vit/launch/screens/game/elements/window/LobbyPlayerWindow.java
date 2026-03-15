package de.rayzs.vit.launch.screens.game.elements.window;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.utils.ImageUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class LobbyPlayerWindow extends GUI implements PlayerWindow {


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

    public LobbyPlayerWindow(final Player player) {
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

        final JLabel nameLabel = new JLabel(shortenPlayerName(player));
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        left.add(nameLabel);
        bannerInner.add(left, BorderLayout.SOUTH);
        banner.add(bannerInner, BorderLayout.CENTER);

        final CardLayout cardLayout = new CardLayout();
        final JPanel body = new JPanel(cardLayout);
        body.setBackground(Colors.BACKGROUND.get());


        final JPanel historyPanel;
        if (player.playedMatches().length == 0) {
            historyPanel = new JPanel(new GridBagLayout());
            historyPanel.setBackground(Colors.BACKGROUND.get());

            final JLabel emptyLabel = new JLabel("No matches found!");
            emptyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            emptyLabel.setForeground(Colors.TEXT_FOREGROUND.get());

            historyPanel.add(emptyLabel);
        } else {
            historyPanel = new JPanel();
            historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

            historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            historyPanel.setBackground(Colors.BACKGROUND.get());

            for (final Match match : player.playedMatches()) {
                historyPanel.add(createMatchBanner(match));
                historyPanel.add(Box.createVerticalStrut(10));
            }
        }


        final JScrollPane historyScroll = new JScrollPane(historyPanel);
        historyScroll.getVerticalScrollBar().setUnitIncrement(10);
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

        body.add(historyScroll, "history");

        contentPane.add(banner, BorderLayout.NORTH);
        contentPane.add(body, BorderLayout.CENTER);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(contentPane);
    }


    private JPanel createMatchBanner(final Match match) {
        final JPanel banner = new JPanel() {
            private final Image mapImage = ImageUtils.darkenImage(
                    ImageUtils.cropImage(
                            VIT.get()
                                    .getImageProvider().getMaps()
                                    .getImage(match.map().mapId()).getImage(),
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
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        banner.setLayout(new GridBagLayout());



        final boolean isTie = match.stats().wonRounds() == match.stats().lostRounds();

        final Color textColor = isTie
                ? Colors.PLAYER_MATCH_TIE.get()
                : match.stats().won()
                ? Colors.PLAYER_MATCH_WON.get()
                : Colors.PLAYER_MATCH_LOST.get();

        final JLabel text = new JLabel(
                matchDisplay.formatted(
                        textColor.getRed(),
                        textColor.getGreen(),
                        textColor.getBlue(),
                        match.stats().wonRounds(),
                        isTie
                                ? "TIE" : match.stats().won()
                                ? "WON" : "LOST",
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
    @Override
    public void show(final int x, final int y) {
        setLocation(x, y);
        setVisible(true);
    }
}