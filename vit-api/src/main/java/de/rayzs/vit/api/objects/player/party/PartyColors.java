package de.rayzs.vit.api.objects.player.party;

import java.awt.*;

public class PartyColors {

    // Constant colors
    private static final Color[] COLORS = new Color[] {
            new Color(145, 59, 211),
            new Color(92, 190, 19),
            new Color(70, 140, 230),
            new Color(220, 90, 170),
            new Color(255, 140, 60),
            new Color(50, 200, 170),
            new Color(200, 200, 90)
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
