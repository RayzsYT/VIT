package de.rayzs.vit.launch.guis;

import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.elements.BeautifiedButton;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsGUI extends GUI {


    private final Map<Section, JButton> sectionButtons = new HashMap<>();

    private final JPanel contentPanel;

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


        final Section guiSection = new Section("GUI");
        guiSection.addTextSetting("Name", "name", "");
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addTextSetting("Description", "description", "");
        guiSection.addTextSetting("Description", "description", "");
        guiSection.addTextSetting("Description", "description", "");
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);
        guiSection.addCheckBoxSetting("pep2", false);

        final Section settingsSection = new Section("Settings");
        settingsSection.addTextSetting("Lol", "sss", "");
        settingsSection.addTextSetting("Skrr", "eofijw", "");
        settingsSection.addCheckBoxSetting("pep1", true);
        settingsSection.addCheckBoxSetting("pep2", false);

        sidebar.add(createSidebarButton(guiSection));
        sidebar.add(createSidebarButton(settingsSection));

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);


        switchSection(guiSection);
    }

    private JButton createSidebarButton(final Section section) {
        final JButton button = new BeautifiedButton(
                section.name,
                Colors.SETTINGS_SECTION_BACKGROUND,
                Colors.SETTINGS_SECTION_FOREGROUND,
                Colors.SETTINGS_SECTION_HOVER,
                Colors.SETTINGS_SECTION_PRESSED,
                Colors.SETTINGS_SECTION_BACKGROUND
        );

        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        button.setBackground(Colors.SETTINGS_SECTION_BACKGROUND.get());
        button.setForeground(Colors.SETTINGS_SECTION_FOREGROUND.get());

        button.addActionListener(e -> switchSection(section));

        sectionButtons.put(section, button);
        return button;
    }

    private void switchSection(Section section) {

        for (Map.Entry<Section, JButton> entry : sectionButtons.entrySet()) {
            if (entry.getKey() != section) {

                entry.getValue().setEnabled(true);
                entry.getValue().setBackground(Colors.SETTINGS_SECTION_BACKGROUND.get());

                continue;
            }

            entry.getValue().setEnabled(false);
            entry.getValue().setBackground(Colors.SETTINGS_SECTION_HOVER.get());
        }


        contentPanel.removeAll();

        contentPanel.add(section.getPanel(), BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class Section {

        private final String name;
        private final JPanel panel;
        private final JScrollPane scrollPane;

        private final Map<String, JTextArea> textAreas = new HashMap<>();
        private final Map<String, JCheckBox> checkBoxes = new HashMap<>();

        public Section(String name) {
            this.name = name;

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

        }

        public JScrollPane getPanel() {
            return scrollPane;
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
                final String name,
                final String placeholder,
                final String defaultText
        ) {
            int row = panel.getComponentCount() / 2;

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

            textAreas.put(name, textArea);

            final GridBagConstraints left = createConstraints(row);
            panel.add(label, left);

            final GridBagConstraints right = createConstraints(row);

            right.gridx = 1;
            right.fill = GridBagConstraints.HORIZONTAL;
            right.weightx = 1;

            panel.add(textArea, right);
        }

        public void addCheckBoxSetting(
                final String name,
                final boolean defaultValue
        ) {
            int row = panel.getComponentCount() / 2;

            final JLabel label = new JLabel(name);
            label.setForeground(Colors.SETTINGS_FOREGROUND.get());

            final JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(Colors.SETTINGS_BOX_BACKGROUND.get());
            checkBox.setSelected(defaultValue);


            checkBoxes.put(name, checkBox);

            final GridBagConstraints left = createConstraints(row);
            panel.add(label, left);

            final GridBagConstraints right = createConstraints(row);
            right.gridx = 1;
            panel.add(checkBox, right);
        }
    }
}