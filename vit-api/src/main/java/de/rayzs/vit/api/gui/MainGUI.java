package de.rayzs.vit.api.gui;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.file.FileDir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

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

        setResizable(true);
        relocateToLastLocation();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                updateLastLocation();
            }
        });


        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setBackground(Colors.BACKGROUND.get());


        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Colors.BACKGROUND.get());

        menuBar.add(createMenu("General",
                item -> {
                    item.setText("Save Match");

                    item.addActionListener(action -> {
                        PopupGUI.create("", "Alright", "Just placeholder. Still in development")
                                .relocateToLastLocation(300, 300);
                    });

                }, item -> {
                    item.setText("Load Match");

                    item.addActionListener(action -> {
                        PopupGUI.create("", "Alright", "Just placeholder. Still in development")
                                .relocateToLastLocation(300, 300);
                    });

                }, item -> {
                    item.setText("Settings");

                    item.addActionListener(action -> {
                        PopupGUI.create("", "Alright", "Just placeholder. Still in development")
                                .relocateToLastLocation(300, 300);
                    });

                }, item -> {
                    item.setText("Need Help?");

                    item.addActionListener(action -> {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/RayzsYT/VIT/wiki"));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                }
        ));

        menuBar.add(createMenu("Addons",
                item -> {
                    item.setText("Open addons folder");

                    item.addActionListener(action -> {
                        try {
                            Desktop.getDesktop().open(FileDir.ADDONS.getFolder());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                }, item -> {
                    item.setText("Reload all addons");

                    item.addActionListener(action -> {
                        final long start = System.currentTimeMillis();

                        VIT.get().getAddonManager().unloadAddons();
                        VIT.get().getAddonManager().loadAddons();


                        final int loadedAddons = VIT.get().getAddonManager().getLoadedAddons().size();

                        System.out.println("Done! Loaded "
                                + loadedAddons
                                + " addons in "
                                + (System.currentTimeMillis() - start) + "ms!"
                        );
                    });

                }
        ));

        menuBar.add(createMenu("Issues",
                item -> {
                    item.setText("Open current logs");

                    item.addActionListener(action -> {
                        for (final File file : FileDir.LOGS.getFolder().listFiles()) {
                            if (file.isFile() && file.getName().endsWith(".txt")) {

                                try {
                                    Desktop.getDesktop().browseFileDirectory(file);
                                } catch (Exception exception) {

                                    // Called on Windows 10 since Java Windows 10
                                    // somehow has some issues regarding the method
                                    // called above.

                                    try {
                                        Desktop.getDesktop().open(file);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                break;
                            }
                        }
                    });

                }, item -> {
                    item.setText("Report issue");

                    item.addActionListener(action -> {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/RayzsYT/VIT/issues"));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                }
        ));

        this.setJMenuBar(menuBar);


        this.disclaimerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.disclaimerPanel.setBackground(GUI.Colors.BACKGROUND.get());

        final JLabel disclaimerLabel = new JLabel(DISCLAIMER.formatted(VITAPI.getVersion()));
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

    private JMenu createMenu(final String title, Consumer<JMenuItem>... items) {
        final JMenu menu = new JMenu(title);

        menu.setOpaque(true);
        menu.setBackground(Colors.BACKGROUND.get());
        menu.setForeground(Colors.TEXT_FOREGROUND.get());

        menu.getPopupMenu().setBorder(null);

        for (final Consumer<JMenuItem> consumer : items) {
            final JMenuItem item = new JMenuItem();

            item.setOpaque(true);
            item.setBackground(Colors.BACKGROUND.get());
            item.setForeground(Colors.TEXT_FOREGROUND.get());

            consumer.accept(item);
            menu.add(item);
        }

        return menu;
    }
}
