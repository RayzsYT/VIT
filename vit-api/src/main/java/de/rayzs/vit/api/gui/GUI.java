package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.image.SystemImages;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GUI extends JFrame {

    protected static final String TITLE = "VIT | %s";

    protected GUI(
            final String title,
            final int width,
            final int height
    ) {
        super(TITLE.formatted(title));

        setIconImage(SystemImages.LOGO.getDisplayImage().getImage());

        setLocationRelativeTo(null);
        setResizable(false);

        setSize(width, height);
    }

    public void relocateToLastLocation() {
        relocateToLastLocation(0, 0);
    }

    public void relocateToLastLocation(
            final int xOffset,
            final int yOffset
    ) {
        final Configuration settings = VIT.get().getSettings();
        final JSONObject lastLocation = settings.get().getJSONObject("last-location");

        final int x = lastLocation.getInt("x");
        final int y = lastLocation.getInt("y");

        if (x == 0 && y == 0) {
            return;
        }

        setLocation(
                x + xOffset,
                y + yOffset
        );
    }

    public void updateLastLocation() {
        final Configuration settings = VIT.get().getSettings();
        final JSONObject lastLocation = settings.get().getJSONObject("last-location");

        final int x = getLocationOnScreen().x;
        final int y = getLocationOnScreen().y;

        lastLocation.put("x", x);
        lastLocation.put("y", y);

        try {
            settings.save();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public enum Colors {

        BACKGROUND                                  (41, 41, 61),
        BORDER                                      (64, 64, 64),

        TOOLTIP_FOREGROUND                          (Color.WHITE),
        TOOLTIP_BACKGROUND                          (36, 36, 53),

        TEXT_FOREGROUND                             (Color.WHITE),
        SELECTION_BACKGROUND                        (Color.WHITE),

        STATS_TEXT_FOREGROUND                       (204, 204, 204),

        PROGRESS_BAR_SELECTED_TEXT                  (74, 8, 12),
        PROGRESS_BAR_FOREGROUND                     (236, 97, 108),
        PROGRESS_BAR_BACKGROUND                     (BACKGROUND),


        MAIN_CONTROL_BUTTON                         (206, 62, 62),

        LOBBY_ROLE_ATTACKING                        (248, 155, 155),
        LOBBY_ROLE_DEFENDING                        (161, 196, 255),

        CONTROL_COMBOBOX_ARROW                      (Color.BLACK),
        CONTROL_COMBOBOX_BACKGROUND                 (206, 62, 62),
        CONTROL_COMBOBOX_HOVER                      (218, 108, 108),
        CONTROL_COMBOBOX_SCROLL_BACKGROUND          (206, 62, 62),
        CONTROL_COMBOBOX_SCROLL_FOREGROUND          (105, 30, 30),

        CONTROL_BUTTON_BACKGROUND                   (206, 62, 62),
        CONTROL_BUTTON_BACKGROUND_HOVER             (129, 27, 27),
        CONTROL_BUTTON_BACKGROUND_PRESSED           (126, 2, 2),
        CONTROL_BUTTON_BACKGROUND_RELEASED          (CONTROL_BUTTON_BACKGROUND),

        PLAYER_MATCH_WON                            (209, 254, 200),
        PLAYER_MATCH_TIE                            (245, 221, 162),
        PLAYER_MATCH_LOST                           (248, 170, 170),

        PLAYER_SCROLLBAR                            (67, 67, 99),

        PLAYER_BUTTON_BACKGROUND                    (206, 62, 62),
        PLAYER_BUTTON_BACKGROUND_HOVER              (129, 27, 27),
        PLAYER_BUTTON_BACKGROUND_PRESSED            (126, 2, 2),
        PLAYER_BUTTON_BACKGROUND_RELEASED           (PLAYER_BUTTON_BACKGROUND),

        BANNER_ATTACKER                             (206, 62, 62),
        BANNER_DEFENDER                             (59, 110, 245),
        BANNER_BACKGROUND                           (47, 47, 67),
        BANNER_BORDER                               (0, 0, 0, 120),


        BUTTON_OPTION_HOVER                         (255, 93, 104),
        BUTTON_OPTION_PRESSED                       (80, 80, 96),
        BUTTON_OPTION_RELEASED                      (64, 64, 96),
        BUTTON_OPTION_BACKGROUND                    (64, 64, 96),


        BUTTON_MAIN_HOVER                           (255, 93, 104),
        BUTTON_MAIN_PRESSED                         (80, 80, 96),
        BUTTON_MAIN_RELEASED                        (64, 64, 96),
        BUTTON_MAIN_BACKGROUND                      (64, 64, 96);


        private final Color color;

        Colors(final Colors color) {
            this(color.get().getRGB());
        }

        Colors(final Color color) {
            this(color.getRGB());
        }

        Colors(final int rgb) {
            this.color = new Color(rgb);
        }

        Colors(final int r, final int g, final int b) {
            this(r, g, b, 255);
        }

        Colors(final int r, final int g, final int b, final int a) {
            this.color = new Color(r, g, b, a);
        }

        public Color get() {
            return color;
        }
    }
}
