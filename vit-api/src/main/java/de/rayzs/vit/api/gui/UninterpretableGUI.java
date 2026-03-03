package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;

public class UninterpretableGUI extends GUI {


    /**
     * Create an uninterpretable gui.
     *
     * @param title Title of the gui.
     * @param text Text of the gui.
     *
     * @return Created gui.
     */
    public static UninterpretableGUI create(final String title, final String... text) {
        return new UninterpretableGUI(title, text);
    }


    private final JTextArea textArea;

    private UninterpretableGUI(final String title, final String... text) {
        super(title, 400, 120);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BACKGROUND.get());


        // Create text
        this.textArea = new JTextArea(String.join("\n", text));
        this.textArea.setWrapStyleWord(true);
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.textArea.setFocusable(false);
        this.textArea.setBackground(Colors.BACKGROUND.get());
        this.textArea.setForeground(Colors.TEXT_FOREGROUND.get());

        this.textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        this.textArea.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));


        // Image logo
        JLabel logo = new JLabel();
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logo.setIcon(SystemImages
                .LOGO
                .getDisplayImage()
                .getIcon(80, 80, Image.SCALE_SMOOTH)
        );

        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);


        panel.add(this.textArea, BorderLayout.CENTER);
        panel.add(logo, BorderLayout.EAST);

        setContentPane(panel);
        setVisible(true);
    }

    public void updateText(final String... text) {
        this.textArea.setText(String.join("\n", text));
    }
}
