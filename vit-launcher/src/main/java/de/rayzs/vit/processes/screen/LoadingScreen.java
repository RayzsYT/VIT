package de.rayzs.vit.processes.screen;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.MainGUI;
import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends Screen {

    private JLabel textLabel;

    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        final JPanel contentPane = gui.getContentPane();

        gui.setTitle("Loading...");

        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GUI.Colors.BACKGROUND.get());


        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GUI.Colors.BACKGROUND.get());


        // Push content down instead of bottom
        panel.add(Box.createVerticalGlue());



        this.textLabel = new JLabel("Loading...");
        this.textLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        this.textLabel.setForeground(Color.WHITE);
        this.textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        panel.add(this.textLabel);
        panel.add(Box.createVerticalStrut(30));


        final JLabel logo = new JLabel(
                SystemImages.LOADING
                        .getDisplayImage()
                        .getIcon()
        );


        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logo);


        // Push content up instead of bottom
        panel.add(Box.createVerticalGlue());


        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.revalidate();
    }

    public void updateText(final String text) {
        this.textLabel.setText(text);
    }
}