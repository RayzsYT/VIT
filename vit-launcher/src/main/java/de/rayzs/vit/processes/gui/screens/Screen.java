package de.rayzs.vit.processes.gui.screens;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.MainGUI;

public abstract class Screen {

    public abstract void load(
            final VITAPI api,
            final MainGUI gui
    );

}
