package de.rayzs.vit.launch.screens.game.elements.banners;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.event.events.player.PreCreatePlayerBannerEvent;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.elements.BeautifiedToolTip;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.player.LastSeenDetails;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.utils.ImageUtils;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.api.utils.TimeConverter;
import de.rayzs.vit.launch.screens.game.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public abstract class PlayerBanner {

    private final PlayerBannerType bannerType;

    protected final String livePlayerNameDisplay = String.join("", new String[]{
            "<html>",
            "<span style='color: rgba(%d, %d, %d, 1); font-size: 13px;'><b>%s</b></span>",
            "<span style='color: rgba(%d, %d, %d, 1); font-size: 8px;'> %s</span>",
            "</html>"
    });

    protected final String lobbyPlayerNameDisplay = String.join("", new String[]{
            "<html>",
            "<span style='color: rgba(%d, %d, %d, 1); font-size: 15px;'><b>%s</b></span>",
            "<span style='color: rgba(%d, %d, %d, 1); font-size: 8px;'> %s</span>",
            "</html>"
    });

    protected MouseAdapter playerMouseAction;

    protected Player player;

    protected final VITAPI api;
    protected final Game game;

    protected final JPanel banner;


    // Updatable panels:
    protected final JPanel stripe;

    // Updatable labels:
    protected final JLabel playerName       = new JLabel();
    protected final JLabel playerStats      = new JLabel();


    protected final JLabel playerAgentImage = new JLabel();
    protected final JLabel weaponImage      = new JLabel() {
        @Override
        public JToolTip createToolTip() {
            return new BeautifiedToolTip(this);
        }
    };


    public PlayerBanner(
            final VITAPI api,
            final Game game,
            final Player player,
            final GameScreen screen,
            final PlayerBannerType bannerType
    ) {

        this.api = api;
        this.game = game;
        this.player = player;
        this.bannerType = bannerType;

        // Set default player banner background.
        banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics graphics) {
                final Graphics2D graphics2D = (Graphics2D) graphics;

                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(GUI.Colors.BANNER_BACKGROUND.get());
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        this.stripe = new JPanel() {

            final int partyHeight = 10;

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);

                final Color background = getBackground();
                final Color partyColor = player.party() == null
                        ? background
                        : player.party().partyColor();


                // Top part for showing parties
                graphics.setColor(partyColor);
                graphics.fillRect(0, 0, getWidth(), partyHeight);

                // Bottom part for showing what team role
                graphics.setColor(background);
                graphics.fillRect(0, partyHeight, getWidth(), getHeight() - partyHeight);
            }
        };

        // Default mouse action when clicking on the player banner.
        this.playerMouseAction = new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                switch (event.getButton()) {
                    case MouseEvent.BUTTON1 -> {
                        screen.openPlayerWindow(player, event.getXOnScreen(), event.getYOnScreen());
                    }

                    // Double right-click to copy players' name and tag.
                    case MouseEvent.BUTTON3 -> {
                        if (player.settings().incognito() && event.getClickCount() >= 2) return;

                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                new StringSelection(player.name()),
                                null
                        );

                    }

                    // Open player stats on tracker.gg
                    case MouseEvent.BUTTON2 -> {
                        if (player.settings().incognito()) return;

                        final String[] nameSplit = player.name()
                                .replace(" ", "")
                                .split("#");

                        try {
                            Desktop.getDesktop().browse(new URI("https://tracker.gg/valorant/profile/riot/" + nameSplit[0] + "%23" + nameSplit[1] + "/"));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    /**
     * Get banner.
     *
     * @return Banner.
     */
    public JPanel getBanner() {
        return banner;
    }

    /**
     * Update certain parts of the player banner.
     * The name, agent image, statistics, and levels.
     *
     * @param newPlayerObj New player object.
     */
    public void updatePlayer(final Player newPlayerObj, final boolean callEvent) {
        if (callEvent) {
            final PreCreatePlayerBannerEvent playerBannerEvent = api.getEventManager().call(new PreCreatePlayerBannerEvent(newPlayerObj));
            this.player = playerBannerEvent.getPlayer();
        } else this.player = newPlayerObj;


        // Set player name
        playerName.setText(formatPlayerName(player));


        // Set player statistics
        final int winRate = (int) (player.stats().winRate() * 100);
        final int headshotRate = (int) (player.stats().headShotRate() * 100);

        final String levelText = player.settings().levelHidden()
                ? api.getGuiSettings().getOrSet("menus.in-game.player-statistics.level.hidden", "?")
                : api.getGuiSettings().getOrSet("menus.in-game.player-statistics.level.shown", "%level%")
                .replace("%level%", String.valueOf(player.level()));

        final String rrText = player.competitive() != null
                ? api.getGuiSettings().getOrSet("menus.in-game.player-statistics.rr.current", "%rr%")
                .replace("%rr%", String.valueOf(player.competitive().rr()))
                : api.getGuiSettings().getOrSet("menus.in-game.player-statistics.rr.none", "/");

        final String lastGainedText = player.competitive() != null && player.competitive().latestMatch() != null
                ? api.getGuiSettings().getOrSet("menus.in-game.player-statistics.rr.last", " (%last_received_rr%)")
                .replace("%last_received_rr%", StringUtils.formatNumber(player.competitive().latestMatch().compMatchResult().rr()))
                : "";

        final String statsDisplay = api.getGuiSettings().getOrSet(
                "menus.in-game.player-statistics.stats-format",
                "Lvl.: %level% | RR: %rr% | WR: %winrate%% | HS: %headshotrate%%"
        );

        playerStats.setText(StringUtils.replace(statsDisplay,
                "%level%", levelText,
                "%rr%", rrText + lastGainedText,
                "%winrate%", player.playedMatches().length != 0
                        ? api.getGuiSettings().getOrSet("menus.in-game.player-statistics.win-rate.available", "%win_rate%")
                        .replace("%win_rate%", String.valueOf(winRate))
                        : api.getGuiSettings().getOrSet("menus.in-game.player-statistics.win-rate.not-available", "?"),
                "%headshotrate%", player.playedMatches().length != 0
                        ? api.getGuiSettings().getOrSet("menus.in-game.player-statistics.headshot-rate.available", "%headshot_rate%")
                        .replace("%headshot_rate%", String.valueOf(headshotRate))
                        : api.getGuiSettings().getOrSet("menus.in-game.player-statistics.headshot-rate.not-available", "?")
        ));


        // Update stripe:
        final Color teamColor = switch (player.team()) {
            case ATTACK -> GUI.Colors.BANNER_ATTACKER.get();
            case DEFEND -> GUI.Colors.BANNER_DEFENDER.get();
        };

        stripe.setBackground(teamColor);


        // --- Everything up 'til here is for LIVE only

        if (bannerType != PlayerBannerType.LIVE) {
            return;
        }


        // Update player weapon skin:
        weaponImage.setToolTipText(player.inventory().getWeaponSkinName(api.getSelectedWeapon()));
        weaponImage.setIcon(new ImageIcon(ImageUtils.rescale(
                player.inventory().getWeaponSkin(api.getSelectedWeapon()).getImage(),
                140,
                40
        )));


        // Update player agent image:
        playerAgentImage.setIcon(player.agent().getImage().getIcon(
                70,
                70,
                Image.SCALE_SMOOTH
        ));
    }



    /**
     * Format the player name.
     *
     * @param player Player.
     * @return Formatted player name.
     */
    protected String formatPlayerName(final Player player) {
        final Team team = player.team();
        final String playerNameDisplay = bannerType == PlayerBannerType.LIVE
                ? livePlayerNameDisplay : lobbyPlayerNameDisplay;


        final LastSeenDetails lastSeenDetails = player.lastSeenDetails();
        final String playerName = player.name();


        // Build 'last seen' text if possible.
        final String lastSeenText;
        if (lastSeenDetails != null) {
            final String lastSeenTimeText = TimeConverter.timeToStrConverter(lastSeenDetails.lastSeenTime());
            final String agentName = lastSeenDetails.agent() != null ? lastSeenDetails.agent().getAgentName() : "?";

            final String lastSeenDisplay = api.getGuiSettings().getOrSet(
                    "menus.in-game.player-statistics.last-seen-format",
                    " %time% agp | %map%, %agent%"
            );


            lastSeenText = StringUtils.replace(lastSeenDisplay,
                    "%time%", lastSeenTimeText,
                    "%map%", lastSeenDetails.map().mapName(),
                    "%agent%", agentName
            );

        } else lastSeenText = "";



        // In case the player being formatted is the
        // user of the program.
        if (playerName.equalsIgnoreCase(game.self().name())) {
            return playerNameDisplay.formatted(
                    250,
                    255,
                    181,
                    "You",
                    0, 0, 0, "" // Self player will be ignored anyway
            );
        }


        // Color for 'last seen' display
        final int lastSeenR = 169;
        final int lastSeenG = 169;
        final int lastSeenB = 169;


        // Formating to the corresponding color, depending
        // on what team the player is on.
        return switch (team) {
            case ATTACK -> playerNameDisplay.formatted(
                    255, 99, 71, playerName,
                    lastSeenR, lastSeenG, lastSeenB, lastSeenText
            );
            case DEFEND -> playerNameDisplay.formatted(
                    0, 255, 255, playerName,
                    lastSeenR, lastSeenG, lastSeenB, lastSeenText
            );
        };
    }


    protected enum PlayerBannerType {
        LIVE, LOBBY
    }
}
