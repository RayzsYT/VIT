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

    private static Season ACTIVE_SEASON_EPISODE = null, ACTIVE_SEASON_ACT = null;


    /**
     * Get a season by its id.
     *
     * @param id Season id.
     * @return Found {@link Season} or NULL if nothing's found.
     */
    public static Season getSeasonById(final String id) {
        return SEASON_IDS.get(id);
    }

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

        switch (season.type) {
            case ACT -> ACTIVE_SEASON_ACT = season;
            case EPISODE -> ACTIVE_SEASON_EPISODE = season;
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

    /**
     * Get active season act.
     *
     * @return Active season act.
     */
    public static Season getActiveAct() {
        return ACTIVE_SEASON_ACT;
    }

    /**
     * Get active season episode.
     *
     * @return Active season episode.
     */
    public static Season getActiveEpisode() {
        return ACTIVE_SEASON_EPISODE;
    }
}
