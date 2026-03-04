package de.rayzs.vit.api.gui;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends GUI {

    private JPanel contentPane;

    public MainGUI(String title) {
        super(title, 1000, 900);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setBackground(Colors.BACKGROUND.get());

        setContentPane(this.contentPane);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(TITLE.formatted(title));
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    public void reset() {
        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setBackground(Colors.BACKGROUND.get());

        setContentPane(this.contentPane);
    }
}
