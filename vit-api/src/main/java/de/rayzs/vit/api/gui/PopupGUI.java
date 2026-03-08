package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.gui.elements.BeautifiedButton;
import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;

public class PopupGUI extends GUI {


    /**
     * Create an option gui.
     *
     * @param title Title of the gui.
     * @param text Text of the gui.
     *
     * @return Created gui.
     */
    public static PopupGUI create(
            final String title,
            final String ok,
            final String... text
    ) {
        return new PopupGUI(title, ok, text);
    }


    private PopupGUI(
            final String title,
            final String ok,
            final String... text
    ) {
        super(title, 400, 180);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BACKGROUND.get());


        // Create text
        final JTextArea textArea = new JTextArea(String.join("\n", text));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        textArea.setBackground(Colors.BACKGROUND.get());
        textArea.setForeground(Colors.TEXT_FOREGROUND.get());

        textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));


        // Image logo
        final JLabel logo = new JLabel();
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logo.setIcon(SystemImages
                .LOGO
                .getDisplayImage()
                .getIcon(80, 80, Image.SCALE_SMOOTH)
        );

        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);


        // Create buttons
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Colors.BACKGROUND.get());

        final JButton okButton = createButton(ok);


        okButton.addActionListener(event -> {
            dispose();
        });

        buttonPanel.add(okButton);


        panel.add(textArea, BorderLayout.CENTER);
        panel.add(logo, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);
    }


    private JButton createButton(final String text) {
        final JButton button = new BeautifiedButton(
                text,
                Colors.BUTTON_OPTION_BACKGROUND,
                Colors.TEXT_FOREGROUND,
                Colors.BUTTON_OPTION_HOVER,
                Colors.BUTTON_OPTION_PRESSED,
                Colors.BUTTON_OPTION_RELEASED
        );

        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        return button;
    }
}
