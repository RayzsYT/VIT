package de.rayzs.vit.processes.gui;

import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.objects.player.Player;

import javax.swing.*;

public class PlayerWindow extends GUI {

    private final Player player;

    public PlayerWindow(final Player player) {
        super(player.name(), 500, 600);

        this.player = player;
        final JPanel contentPane = new JPanel();

        contentPane.setBackground(Colors.BACKGROUND.get());


        // Implementation showing skins or match history.
        // Tbh, I'm still not sure how exactly I'd like to
        // do it in the end. Maybe a switch button on the top?


        setContentPane(contentPane);
    }

    /**
     * Show the player window at the mouse
     * coordination.
     *
     * @param x Mouse x location.
     * @param y Mouse y location.
     */
    public void show(final int x, final int y) {
        setLocation(x, y);
        setVisible(true);
    }
}
