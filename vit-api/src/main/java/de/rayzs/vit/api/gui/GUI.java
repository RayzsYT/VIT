package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;

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



    public enum Colors {

        BACKGROUND                          (41, 41, 61),
        BORDER                              (64, 64, 64),

        TOOLTIP_FOREGROUND                  (Color.WHITE),
        TOOLTIP_BACKGROUND                  (36, 36, 53),

        TEXT_FOREGROUND                     (Color.WHITE),
        SELECTION_BACKGROUND                (Color.WHITE),

        STATS_TEXT_FOREGROUND              (204, 204, 204),


        PROGRESS_BAR_SELECTED_TEXT          (74, 8, 12),
        PROGRESS_BAR_FOREGROUND             (236, 97, 108),
        PROGRESS_BAR_BACKGROUND             (BACKGROUND),


        MAIN_CONTROL_BUTTON                 (206, 62, 62),


        LOBBY_ROLE_ATTACKING                (new Color(248, 155, 155)),
        LOBBY_ROLE_DEFENDING                (new Color(161, 196, 255)),

        BANNER_ATTACKER                     (206, 62, 62),
        BANNER_DEFENDER                     (59, 110, 245),
        BANNER_BACKGROUND                   (47, 47, 67),
        BANNER_BORDER                       (0, 0, 0, 120),


        BUTTON_OPTION_HOVER                 (255, 93, 104),
        BUTTON_OPTION_PRESSED               (80, 80, 96),
        BUTTON_OPTION_RELEASED              (64, 64, 96),
        BUTTON_OPTION_BACKGROUND            (64, 64, 96),


        BUTTON_MAIN_HOVER                    (255, 93, 104),
        BUTTON_MAIN_PRESSED                  (80, 80, 96),
        BUTTON_MAIN_RELEASED                 (64, 64, 96),
        BUTTON_MAIN_BACKGROUND               (64, 64, 96);


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
