package de.rayzs.vit.launch.guis;

import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.PopupGUI;
import de.rayzs.vit.api.gui.elements.BeautifiedButton;
import de.rayzs.vit.api.settings.Settings;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SettingsGUI extends GUI {


    private static final Map<Section, JButton> SECTIONS = new HashMap<>();

    private static JButton createButton(final String buttonText) {
        final JButton button = new BeautifiedButton(
                buttonText,
                Colors.SETTINGS_SECTION_BACKGROUND,
                Colors.SETTINGS_SECTION_FOREGROUND,
                Colors.SETTINGS_SECTION_HOVER,
                Colors.SETTINGS_SECTION_PRESSED,
                Colors.SETTINGS_SECTION_BACKGROUND
        );

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }


    private final JPanel contentPanel;

    private Section currentSection;

    public SettingsGUI() {
        super("Settings", 500, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new BorderLayout());


        final JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(130, 0));
        sidebar.setBackground(Colors.SETTINGS_SECTION_BACKGROUND.get());


        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Colors.SETTINGS_SIDEBAR_BACKGROUND.get());


        final JButton saveSettingsButton = new BeautifiedButton(
                "Apply Changes",
                Colors.SETTINGS_SAVE_BACKGROUND,
                Colors.SETTINGS_SAVE_FOREGROUND,
                Colors.SETTINGS_SAVE_HOVER,
                Colors.SETTINGS_SAVE_PRESSED,
                Colors.SETTINGS_SAVE_BACKGROUND
        );

        saveSettingsButton.setFocusPainted(false);
        saveSettingsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        saveSettingsButton.addActionListener(e -> {
            SECTIONS.keySet().forEach(Section::applyChanges);

            PopupGUI.create(
                    "Done!",
                    "Alright!",
                    "Setting changes saved!"
            ).setLocation(
                    this.getLocation().x + 100,
                    this.getLocation().y + 70
            );


        });



        final Section scanSection = new Section("Scan");

        scanSection.addNumberSetting(
                Settings.SCAN_PLAYER_MATCHES_AMOUNT,
                "Amount of Player Matches",
                Settings.SCAN_PLAYER_MATCHES_AMOUNT.read()
        );

        scanSection.addCheckBoxSetting(
                Settings.SCAN_PLAYER_PARTIES,
                "Scan Player Parties",
                Settings.SCAN_PLAYER_PARTIES.read()
        );



        final Set<Section> sections = SECTIONS.keySet();
        final Iterator<Section> sectionIter = sections.iterator();

        for (int i = 0; i < 10; i++) {

            if (!sectionIter.hasNext()) {

                final JButton button = createButton("");
                button.setEnabled(false);

                sidebar.add(button);
                continue;
            }

            final Section section = sectionIter.next();
            final JButton button = section.getButton();

            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.addActionListener(e -> switchSection(section));

            sidebar.add(button);

        }

        sidebar.add(saveSettingsButton);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);


        switchSection(scanSection);
    }

    private void switchSection(final Section section) {

        for (final Map.Entry<Section, JButton> entry : SECTIONS.entrySet()) {
            if (entry.getKey() != section) {

                entry.getValue().setEnabled(true);
                entry.getValue().setBackground(Colors.SETTINGS_SECTION_BACKGROUND.get());

                continue;
            }

            entry.getValue().setEnabled(false);
            entry.getValue().setBackground(Colors.SETTINGS_SECTION_HOVER.get());
        }


        currentSection = section;
        contentPanel.removeAll();


        contentPanel.add(section.getPanel(), BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private static class Section {

        private final String name;
        private final JPanel panel;
        private final JButton button;

        private final JScrollPane scrollPane;

        private final Map<JComponent, Settings> components = new HashMap<>();


        public Section(String name) {
            this.name = name;

            button = createButton(name);

            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.setBackground(Colors. SETTINGS_BACKGROUND.get());

            scrollPane = new JScrollPane(panel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setBorder(null);


            scrollPane.setHorizontalScrollBarPolicy(
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {

                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = Colors.SETTINGS_SCROLLBAR.get();
                    this.trackColor = Colors.BACKGROUND.get();
                }

                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }

                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }

                private JButton createZeroButton() {
                    final JButton button = new JButton();

                    button.setPreferredSize(new Dimension(0, 0));
                    return button;
                }
            });


            SECTIONS.put(this, button);
        }


        public void applyChanges() {
            for (Map.Entry<JComponent, Settings> entry : components.entrySet()) {
                final JComponent component = entry.getKey();
                final Settings settings = entry.getValue();

                if (component instanceof JTextArea textArea) {
                    settings.update(textArea.getText());
                } else if (component instanceof JSpinner spinner) {

                    if (spinner.getValue() instanceof Number number) {
                        settings.update(number.intValue());
                    }

                } else if (component instanceof JCheckBox checkBox) {
                    settings.update(checkBox.isSelected());
                }
            }
        }


        public JScrollPane getPanel() {
            return scrollPane;
        }

        public JButton getButton() {
            return button;
        }

        private GridBagConstraints createConstraints(final int y) {
            final GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = y;

            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.anchor = GridBagConstraints.WEST;

            return gbc;
        }

        public void addTextSetting(
                final Settings setting,
                final String name,
                final String placeholder,
                final String defaultText
        ) {
            final int row = panel.getComponentCount() / 2;

            final JLabel label = new JLabel(name);
            label.setForeground(Colors.SETTINGS_FOREGROUND.get());

            final JTextArea textArea = new JTextArea(defaultText);
            textArea.setForeground(Colors.SETTINGS_FOREGROUND.get());
            textArea.setBackground(Colors.SETTINGS_BOX_BACKGROUND.get());
            textArea.setToolTipText(placeholder);

            textArea.setRows(1);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            components.put(textArea, setting);

            final GridBagConstraints left = createConstraints(row);
            panel.add(label, left);

            final GridBagConstraints right = createConstraints(row);

            right.gridx = 1;
            right.fill = GridBagConstraints.HORIZONTAL;
            right.weightx = 1;

            panel.add(textArea, right);
        }

        public void addNumberSetting(
                final Settings setting,
                final String name,
                final int defaultVal
        ) {
            final int row = panel.getComponentCount() / 2;

            final JLabel label = new JLabel(name);
            label.setForeground(Colors.SETTINGS_FOREGROUND.get());

            final JSpinner spinner = new JSpinner(new SpinnerNumberModel(defaultVal, 0, 15, 1));
            final JFormattedTextField spinnerTextArea = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            spinnerTextArea.setEditable(false);

            spinner.setForeground(Colors.SETTINGS_FOREGROUND.get());
            spinner.setBackground(Colors.SETTINGS_BOX_BACKGROUND.get());

            spinner.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            components.put(spinner, setting);

            final GridBagConstraints left = createConstraints(row);
            panel.add(label, left);

            final GridBagConstraints right = createConstraints(row);

            right.gridx = 1;
            right.fill = GridBagConstraints.HORIZONTAL;
            right.weightx = 1;

            panel.add(spinner, right);
        }

        public void addCheckBoxSetting(
                final Settings setting,
                final String name,
                final boolean defaultValue
        ) {
            final int row = panel.getComponentCount() / 2;

            final JLabel label = new JLabel(name);
            label.setForeground(Colors.SETTINGS_FOREGROUND.get());

            final JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(Colors.SETTINGS_BOX_BACKGROUND.get());
            checkBox.setSelected(defaultValue);


            components.put(checkBox, setting);

            final GridBagConstraints left = createConstraints(row);
            panel.add(label, left);

            final GridBagConstraints right = createConstraints(row);
            right.gridx = 1;
            panel.add(checkBox, right);
        }
    }
}