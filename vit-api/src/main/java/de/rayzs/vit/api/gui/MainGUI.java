package de.rayzs.vit.api.gui;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends GUI {

    public MainGUI(String title) {
        super(title, 1000, 900);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BACKGROUND.get());

        setContentPane(panel);
        setVisible(true);
    }
}
