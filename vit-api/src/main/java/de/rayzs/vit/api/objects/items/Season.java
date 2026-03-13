package de.rayzs.vit.api.objects.items;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public record Season(
        String id,              // Season id
        String name,            // Season name
        SeasonType type,        // Season type
        Season parent           // Parent season (e.g: an act)
) implements Serializable {


    public Season {
        SEASON_IDS.putIfAbsent(id, this);
    }


    // season id, season
    private static final HashMap<String, Season> SEASON_IDS = new HashMap<>();

    // season, is active
    private static final HashMap<Season, Boolean> SEASON_ACTIVITY = new HashMap<>();


    /**
     * Set a season active. Can only be done once
     * for a season and cannot be reverted back.
     *
     * @param season Season to set as active season.
     */
    public static void setActive(final Season season) {
        if (SEASON_ACTIVITY.containsKey(season)) {
            throw new IllegalArgumentException("Season (" + season.name() + ") is already active!");
        }

        SEASON_ACTIVITY.put(season, true);
    }

    /**
     * Return a collection of all active seasons.
     *
     * @return Active seasons.
     */
    public static Collection<Season> getActiveSeasons() {
        return SEASON_ACTIVITY.keySet();
    }

}
