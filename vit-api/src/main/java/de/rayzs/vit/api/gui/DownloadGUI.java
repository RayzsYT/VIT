package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.image.SystemImages;

import javax.swing.*;
import java.awt.*;

public class DownloadGUI extends GUI {


    /**
     * Create a download gui.
     *
     * @param title Title of the gui.
     * @param text Text of the gui.
     *
     * @return Created gui.
     */
    public static DownloadGUI create(final String title, final String... text) {
        return new DownloadGUI(title, text);
    }


    private final JTextArea textArea;
    private final JProgressBar progressBar;

    private DownloadGUI(final String title, final String... text) {
        super(title, 400, 220);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

        this.textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        this.textArea.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));


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


        // Create progress bar
        final JPanel progressBarPanel = new JPanel();
        progressBarPanel.setBackground(Colors.BACKGROUND.get());
        progressBarPanel.setLayout(new BoxLayout(progressBarPanel, BoxLayout.Y_AXIS));
        progressBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setForeground(Colors.PROGRESS_BAR_FOREGROUND.get());
        this.progressBar.setBackground(Colors.PROGRESS_BAR_BACKGROUND.get());

        this.progressBar.setBorder(BorderFactory.createLineBorder(Colors.BORDER.get(), 1));
        this.progressBar.setStringPainted(true);
        this.progressBar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        this.progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected Color getSelectionBackground() {
                return Colors.SELECTION_BACKGROUND.get();
            }

            protected Color getSelectionForeground() {
                return Colors.PROGRESS_BAR_SELECTED_TEXT.get();
            }
        });

        this.progressBar.setPreferredSize(new Dimension(350, 20));
        this.progressBar.setMaximumSize(new Dimension(350, 20));
        progressBarPanel.add(this.progressBar);

        panel.add(this.textArea, BorderLayout.CENTER);
        panel.add(logo, BorderLayout.EAST);
        panel.add(progressBarPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);
    }

    public JProgressBar getProgressBar() {
        return this.progressBar;
    }

    public void updateText(final String... text) {
        this.textArea.setText(String.join("\n", text));
    }
}
