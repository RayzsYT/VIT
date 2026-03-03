package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OptionGUI extends GUI {


    /**
     * Create an option gui.
     *
     * @param title Title of the gui.
     * @param text Text of the gui.
     *
     * @return Created gui.
     */
    public static OptionGUI create(
            final String title,
            final String accept,
            final String deny,
            final String... text
    ) {
        return new OptionGUI(title, accept, deny, text);
    }


    private int response = 0;

    private OptionGUI(
            final String title,
            final String accept,
            final String deny,
            final String... text
    ) {
        super(title, 400, 220);

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

        final JButton acceptButton = createButton(accept);
        final JButton denyButton = createButton(deny);


        acceptButton.addActionListener(event -> {
            this.response = 1;
            dispose();
        });

        denyButton.addActionListener(event -> {
            this.response = -1;
            dispose();
        });

        buttonPanel.add(acceptButton);
        buttonPanel.add(denyButton);


        panel.add(textArea, BorderLayout.CENTER);
        panel.add(logo, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);


        // Freezing 'til any response is made.
        while (this.response == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * What did the user end up clicking.
     * 1  = ACCEPT
     * -1 = DENY
     * 0  = NOTHING
     *
     * @return Response as int.
     */
    public int getResponse() {
        return this.response;
    }

    private JButton createButton(final String text) {
        final JButton button = new JButton(text);

        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.setText(text);

        button.setFocusPainted(false);
        button.setBackground(Colors.BUTTON_BACKGROUND.get());
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(Colors.BUTTON_HOVER.get());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(Colors.BUTTON_BACKGROUND.get());
            }
        });

        button.addChangeListener(event -> {
            final ButtonModel model = button.getModel();
            button.setBackground(model.isPressed()
                    ? Colors.BUTTON_PRESSED.get()
                    : Colors.BUTTON_RELEASED.get()
            );
        });

        return button;
    }
}
