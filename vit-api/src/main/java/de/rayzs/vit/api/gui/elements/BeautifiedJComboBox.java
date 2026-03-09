package de.rayzs.vit.api.gui.elements;

import de.rayzs.vit.api.gui.GUI;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class BeautifiedJComboBox<E> extends JComboBox<E> {

    public BeautifiedJComboBox(
            final E[] items,
            final GUI.Colors selectedBackground,
            final GUI.Colors selectedForeground,
            final GUI.Colors background,
            final GUI.Colors foreground,
            final GUI.Colors ar,
            final GUI.Colors scrollbarForeground,
            final GUI.Colors scrollbarBackground
    ) {
        super(items);


        setFocusable(false);

        setBackground(background.get());
        setForeground(Color.WHITE);

        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        // Change color for box corner area and
        // the symbol on the right side of the text.
        setUI(new BasicComboBoxUI() {

            @Override
            public void paintCurrentValueBackground(
                    final Graphics graphics,
                    final Rectangle bounds,
                    final boolean hasFocus
            ) {
                graphics.setColor(background.get());
            }

            // I disliked the arrows, but I still wanted something to symbolise it.
            @Override
            protected JButton createArrowButton() {
                final JButton button = new BeautifiedButton(
                        "...",
                        background,
                        foreground,
                        background,
                        background,
                        background
                );

                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));

                return button;
            }
        });



        // Changed selection color
        setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    final JList<?> list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus
            ) {

                final Component component = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (isSelected) {
                    component.setBackground(selectedBackground.get());
                    component.setForeground(selectedForeground.get());
                } else {
                    component.setBackground(background.get());
                    component.setForeground(foreground.get());
                }

                return component;
            }
        });


        // Change scrollbar color
        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                final JComboBox<?> combo = (JComboBox<?>) e.getSource();
                final Object child = combo.getAccessibleContext().getAccessibleChild(0);

                if (child instanceof JPopupMenu popup) {
                    final JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
                    final JScrollBar bar = scrollPane.getVerticalScrollBar();


                    bar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));


                    bar.setUI(new BasicScrollBarUI() {

                        @Override
                        protected void configureScrollBarColors() {
                            this.thumbColor = scrollbarForeground.get();
                            this.trackColor = scrollbarBackground.get();
                        }

                        @Override
                        protected JButton createDecreaseButton(final int orientation) {
                            return createEmptyButton();
                        }

                        @Override
                        protected JButton createIncreaseButton(final int orientation) {
                            return createEmptyButton();
                        }


                        // Basically just a button as small as possible to hide
                        // the arrows.
                        private JButton createEmptyButton() {
                            final JButton button = new JButton();

                            button.setPreferredSize(new Dimension(0, 0));
                            button.setMinimumSize(new Dimension(0, 0));
                            button.setMaximumSize(new Dimension(0, 0));

                            return button;
                        }
                    });
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

    }
}
