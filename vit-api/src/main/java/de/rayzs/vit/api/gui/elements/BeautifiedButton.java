package de.rayzs.vit.api.gui.elements;

import de.rayzs.vit.api.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An already formatted and beautified button
 * with the default colors from {@link de.rayzs.vit.api.gui.GUI.Colors}.
 */
public class BeautifiedButton extends JButton {

    public BeautifiedButton() {
        this("");
    }

    public BeautifiedButton(final String text) {
        setBorderPainted(false);
        setFocusPainted(false);

        setContentAreaFilled(false);
        setOpaque(true);

        setText(text);

        setFocusPainted(false);
        setBackground(GUI.Colors.BUTTON_BACKGROUND.get());
        setForeground(GUI.Colors.TEXT_FOREGROUND.get());

        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent event) {
                setBackground(GUI.Colors.BUTTON_HOVER.get());
            }

            @Override
            public void mouseExited(final MouseEvent event) {
                setBackground(GUI.Colors.BUTTON_BACKGROUND.get());
            }
        });

        addChangeListener(event -> {
            setBackground(getModel().isPressed()
                    ? GUI.Colors.BUTTON_PRESSED.get()
                    : GUI.Colors.BUTTON_RELEASED.get()
            );
        });
    }
}
