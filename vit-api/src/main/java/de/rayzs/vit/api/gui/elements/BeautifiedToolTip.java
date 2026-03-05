package de.rayzs.vit.api.gui.elements;

import de.rayzs.vit.api.gui.GUI;

import javax.swing.*;

public class BeautifiedToolTip extends JToolTip {

    public BeautifiedToolTip(final JComponent component) {
        super();

        setComponent(component);
        setBackground(GUI.Colors.TOOLTIP_BACKGROUND.get());
        setForeground(GUI.Colors.TOOLTIP_FOREGROUND.get());
    }
}
