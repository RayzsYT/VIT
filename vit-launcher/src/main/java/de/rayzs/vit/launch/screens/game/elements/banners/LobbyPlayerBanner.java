package de.rayzs.vit.launch.screens.game.elements.banners;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.elements.BeautifiedToolTip;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Season;
import de.rayzs.vit.api.objects.items.Tier;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.launch.screens.game.GameScreen;

import javax.swing.*;
import java.awt.*;

public class LobbyPlayerBanner extends PlayerBanner {


    /**
     * Create JPanel for a player banner
     * showing information of a player.
     *
     * @param api VITAPI.
     * @param game Game.
     * @param player Player.
     * @param screen Current active screen.
     */
    public LobbyPlayerBanner(
            final VITAPI api,
            final Game game,
            final Player player,
            final GameScreen screen
    ) {
        super(api, game, player, screen, PlayerBannerType.LOBBY);


        // Set cursor when entering banner panel
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Open Player window on click
        banner.addMouseListener(playerMouseAction);


        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(450, 170));
        banner.setMinimumSize(new Dimension(450, 170));
        banner.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        stripe.setPreferredSize(new Dimension(4, 0));
        banner.add(stripe, BorderLayout.WEST);

        final JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(GUI.Colors.BANNER_BACKGROUND.get());
        inner.setBorder(BorderFactory.createEmptyBorder(2, 15, 0, 15));

        playerAgentImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        final JPanel agentWrapper = new JPanel(new BorderLayout());
        agentWrapper.setOpaque(false);
        agentWrapper.add(playerAgentImage, BorderLayout.SOUTH);
        inner.add(agentWrapper, BorderLayout.WEST);

        final JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        playerName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        playerName.setForeground(GUI.Colors.TEXT_FOREGROUND.get());
        center.add(playerName);

        center.add(Box.createVerticalStrut(6));

        playerStats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        playerStats.setForeground(GUI.Colors.STATS_TEXT_FOREGROUND.get());
        center.add(playerStats);

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

        final boolean hasCurrentTier = player.competitive() != null;
        final boolean hasPeakTier = hasCurrentTier && player.competitive().seasonTiers() != null;

        final Tier currentTier = hasCurrentTier
                ? player.competitive().currentTier()
                : Tier.UNRANKED;

        final Tier peakTier = hasPeakTier
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


            final Season peakSeason = player.competitive().seasonTiers().getPeakSeason();

            peakRankLabel.setToolTipText("Peak Rank: " + peakTier.getTierName() + " (" + peakSeason.name() + ")");

            rankPanel.add(peakRankLabel);
            rankPanel.add(Box.createHorizontalStrut(8));
        }

        rankPanel.add(currentRankLabel);

        final JPanel rankWrapper = new JPanel(new GridBagLayout());
        rankWrapper.setOpaque(false);
        rankWrapper.add(rankPanel);

        inner.add(rankWrapper, BorderLayout.EAST);

        banner.add(inner, BorderLayout.CENTER);


        updatePlayer(player);
    }
}
