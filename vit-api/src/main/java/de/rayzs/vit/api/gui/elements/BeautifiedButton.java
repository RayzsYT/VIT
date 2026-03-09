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

    public BeautifiedButton(
            final GUI.Colors background,
            final GUI.Colors foreground,
            final GUI.Colors hoverBackground,
            final GUI.Colors pressBackground,
            final GUI.Colors releaseBackground
    ) {
        this("",
                background,
                foreground,
                hoverBackground,
                pressBackground,
                releaseBackground
        );
    }

    public BeautifiedButton(
            final String text,
            final GUI.Colors background,
            final GUI.Colors foreground,
            final GUI.Colors hoverBackground,
            final GUI.Colors pressBackground,
            final GUI.Colors releaseBackground
    ) {
        setBorderPainted(false);
        setFocusPainted(false);

        setContentAreaFilled(false);
        setOpaque(true);

        setText(text);

        setFocusPainted(false);
        setBackground(background.get());
        setForeground(foreground.get());

        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));


        final JButton thisButton = this;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent event) {
                if (thisButton.isEnabled()) {
                    setBackground(hoverBackground.get());
                }
            }

            @Override
            public void mouseExited(final MouseEvent event) {
                if (thisButton.isEnabled()) {
                    setBackground(background.get());
                }
            }
        });

        addChangeListener(event -> {
            setBackground(getModel().isPressed()
                    ? pressBackground.get()
                    : releaseBackground.get()
            );
        });
    }

    @Override
    public JToolTip createToolTip() {
        return new BeautifiedToolTip(this);
    }
}
