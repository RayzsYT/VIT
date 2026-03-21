package de.rayzs.vit.api.objects.player.party;

import java.awt.*;

public class PartyColors {

    // Constant colors
    private static final Color[] COLORS = new Color[] {
            new Color(255, 255, 255),
            new Color(255, 255, 0),
            new Color(142, 0, 173),
            new Color(246, 188, 147),
            new Color(7, 255, 0),
            new Color(222, 138, 189),
            new Color(156, 255, 232)
    };

    /**
     * Gives a color for a party based on its index.
     * Cannot go out of bounce since the parameter is always
     * between 0 and the max color size.
     *
     * @param index Index of the color to choose.
     * @return Corresponding color.
     */
    public static Color getPartyColor(final int index) {
        return COLORS[index % COLORS.length];
    }

}
