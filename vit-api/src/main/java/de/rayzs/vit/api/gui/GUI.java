package de.rayzs.vit.api.gui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private static final String TITLE = "VIT | %s";

    public GUI(final String title, final int width, final int height) {
        super(TITLE.formatted(title));

        setSize(width, height);
        setBackground(Colors.BACKGROUND.get());
    }



    public enum Colors {

        BACKGROUND          (41, 41, 61),
        TEXT_FOREGROUND     (255, 255, 255);


        private final Color color;

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
