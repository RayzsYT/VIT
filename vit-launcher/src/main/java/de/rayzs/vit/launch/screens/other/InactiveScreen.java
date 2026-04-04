package de.rayzs.vit.launch.screens.other;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.launch.guis.MainGUI;
import de.rayzs.vit.api.image.SystemImages;
import de.rayzs.vit.launch.screens.ScreenAbstr;

import javax.swing.*;
import java.awt.*;

public class InactiveScreen extends ScreenAbstr {

    private JLabel textLabel;

    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        gui.reset();
        gui.setTitle("Waiting for VALORANT to start...");


        final JPanel contentPane = gui.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GUI.Colors.BACKGROUND.get());


        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GUI.Colors.BACKGROUND.get());


        // Push content down instead of bottom
        panel.add(Box.createVerticalGlue());



        this.textLabel = new JLabel("VALORANT NOT FOUND!");
        this.textLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        this.textLabel.setForeground(Color.WHITE);
        this.textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        panel.add(this.textLabel);
        panel.add(Box.createVerticalStrut(30));


        final JLabel logo = new JLabel(
                SystemImages.ERROR
                        .getDisplayImage()
                        .getIcon(200, 200, Image.SCALE_SMOOTH)
        );


        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logo);


        // Push content up instead of bottom
        panel.add(Box.createVerticalGlue());


        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }

    public void updateText(final String text) {
        this.textLabel.setText(text);
    }
}