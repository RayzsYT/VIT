package de.rayzs.vit.api.objects.player.competitive;

import de.rayzs.vit.api.objects.items.Season;
import de.rayzs.vit.api.objects.items.Tier;

import java.util.*;

public class SeasonTiers {

    // Empty SeasonStats as dummy in case none was found
    private static final SeasonStats EMPTY_SEASON_STATS = new SeasonStats(
            null, 0, 0, 0, 0,0
    );


    // Season, Stats
    private final Map<Season, SeasonStats> seasonStats = new LinkedHashMap<>();

    // Season, Tier
    private final Map<Season, Tier> seasonTiers = new LinkedHashMap<>();

    private final SeasonStats peakSeasonStats;
    private final Season peakSeason;
    private final Tier peakTier;

    // Season, TierId
    public SeasonTiers(final Map<SeasonStats, Integer> seasonTierIds) {

        // Sort seasons by their tier put them in the seasonTiers map.
        seasonTierIds.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) // Sort: Big -> Small
                .forEach(entry -> {
                    final SeasonStats stats = entry.getKey();
                    final Season season = stats.season();

                    final String tierId = String.valueOf(entry.getValue());
                    final Tier tier = Tier.getTierById(tierId);

                    if (tier == null) {
                        throw new IllegalArgumentException("Tier with id " + tierId + " not found!");
                    }

                    seasonStats.put(season, stats);
                    seasonTiers.put(season, tier);
                });

        final Iterator<Map.Entry<Season, Tier>> iterator = seasonTiers.entrySet().iterator();

        // No peak rank found. So will just ignore entirely.
        if (!iterator.hasNext()) {
            peakTier = null;

            peakSeason = null;
            peakSeasonStats = null;

            return;
        }


        final Map.Entry<Season, Tier> peakEntry = iterator.next();

        peakTier = peakEntry.getValue();
        peakSeason = peakEntry.getKey();
        peakSeasonStats = seasonStats.get(peakSeason);
    }

    /**
     * Get the latest tier the player was in during a certain season.
     * Returns UNRANKED in case nothing could be found.
     *
     * @param season Season.
     *
     * @return Tier.
     */
    public Tier getTierInSeason(final Season season) {
        return this.seasonTiers.getOrDefault(season, Tier.UNRANKED);
    }

    /**
     * Get the session statistics of a certain season.
     *
     * @param season Season.
     *
     * @return Mapped {@link SeasonStats} if it exist. Returns null otherwise.
     */
    public SeasonStats getSessionStats(final Season season) {
        return this.seasonStats.getOrDefault(season, EMPTY_SEASON_STATS);
    }

    /**
     * Get if the player had a peak tier in a season.
     *
     * @return True if the player had a peak tier in a season. False otherwise.
     */
    public boolean hasPeak() {
        return this.peakSeason != null && this.peakTier != null;
    }

    /**
     * Get the peak season of the player,
     * with the highest tier.
     *
     * @return Peak season.
     */
    public Season getPeakSeason() {
        return this.peakSeason;
    }

    /**
     * Get the peak tier of the player,
     * with the highest tier.
     *
     * @return Peak tier.
     */
    public Tier getPeakTier() {
        return this.peakTier;
    }
}
