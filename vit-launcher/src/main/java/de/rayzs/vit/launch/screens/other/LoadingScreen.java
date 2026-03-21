package de.rayzs.vit.launch.screens.other;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.launch.guis.MainGUI;
import de.rayzs.vit.api.image.SystemImages;
import de.rayzs.vit.launch.screens.Screen;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends Screen {

    private final String defaultText = "Waiting";
    private final JLabel textLabel = new JLabel(defaultText);

    @Override
    public void load(final VITAPI api, final MainGUI gui) {
        gui.reset();
        gui.setTitle(this.defaultText);


        final JPanel contentPane = gui.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GUI.Colors.BACKGROUND.get());


        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GUI.Colors.BACKGROUND.get());


        // Push content down instead of bottom
        panel.add(Box.createVerticalGlue());



        this.textLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));
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
        contentPane.add(gui.getDisclaimerPanel(), BorderLayout.SOUTH);
    }

    public void resetText() {
        this.textLabel.setText(this.defaultText);
    }

    public void updateText(final String text) {
        this.textLabel.setText(text);
    }
}