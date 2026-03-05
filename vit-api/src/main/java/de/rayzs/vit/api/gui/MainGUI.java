package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.VITAPI;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends GUI {


    // Disclaimer text. Will be shown on the very bottom of the program.
    private static final String DISCLAIMER = String.join("", new String[] {
            "<html><div style='width:800px; text-align:center; color:rgba(255,255,255,0.2);'>",
            "VIT isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties.<br>",
            "Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.",
            "<br><br>Version v%s",
            "</div></html>"
    });


    private final JPanel disclaimerPanel;
    private JPanel contentPane;


    public MainGUI(final String title) {
        super(title, 1000, 900);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setBackground(Colors.BACKGROUND.get());

        this.disclaimerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.disclaimerPanel.setBackground(GUI.Colors.BACKGROUND.get());

        final JLabel disclaimerLabel = new JLabel(DISCLAIMER.formatted(VIT.get().getVersion()));
        disclaimerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        disclaimerPanel.add(disclaimerLabel);

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

    public JPanel getDisclaimerPanel() {
        return this.disclaimerPanel;
    }

    public void reset() {
        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setBackground(Colors.BACKGROUND.get());

        setContentPane(this.contentPane);
    }
}
