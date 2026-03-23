package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.image.SystemImages;

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

        final int storedX = settings.getInt("last-location.x");
        final int storedY = settings.getInt("last-location.y");

        if (storedX == 0 && storedY == 0) {
            return;
        }


        /*
        This code is supposed to check whether the gui is out of the screen or not.
        Unfortunately, it is kinda off. I'll check it later sometime, but for now, I'll
        ignore this implementation and leave it be.


        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final int screenDevices = graphicsEnvironment.getScreenDevices().length;

        int minX = 0, minY = 0, tMaxX = 0, tMaxY = 0;
        for (GraphicsDevice device : graphicsEnvironment.getScreenDevices()) {
            final Rectangle bounds = device.getDefaultConfiguration().getBounds();

            minX = Math.min(minX, bounds.x);
            minY = Math.min(minY, bounds.y);

            tMaxX = Math.max(tMaxX, bounds.x + bounds.width);
            tMaxY = Math.max(tMaxY, bounds.y + bounds.height);
        }

        final int maxX = ((tMaxX - minX) / screenDevices) + getWidth();
        final int maxY = ((tMaxY - minY) / screenDevices) + getHeight();


        final int x = Math.max(minX, Math.min(storedX + xOffset, maxX));
        final int y = Math.max(minY, Math.min(storedY + yOffset, maxY));
         */

        setLocation(
                xOffset + storedX,
                yOffset + storedY
        );
    }

    public void updateLastLocation() {
        final Configuration settings = VIT.get().getSettings();

        final int x = getLocationOnScreen().x;
        final int y = getLocationOnScreen().y;

        settings.set("last-location.x", x)
                .set("last-location.y", y);

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


        SETTINGS_BACKGROUND                         (41, 41, 61),
        SETTINGS_FOREGROUND                         (TEXT_FOREGROUND),
        SETTINGS_SIDEBAR_BACKGROUND                 (61, 61, 86),
        SETTINGS_SECTION_BOX_BACKGROUND             (41, 41, 61),
        SETTINGS_SECTION_BACKGROUND                 (35, 35, 54),
        SETTINGS_SECTION_FOREGROUND                 (SETTINGS_FOREGROUND),
        SETTINGS_SECTION_HOVER                      (59, 59, 84),
        SETTINGS_SECTION_PRESSED                    (76, 76, 101),
        SETTINGS_BOX_BACKGROUND                     (new Color(109, 109, 138)),
        SETTINGS_SCROLLBAR                          (67, 67, 99),


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
