package de.rayzs.vit.api.objects.items;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.image.DisplayImage;

public enum Tier {

    UNRANKED            ("Unranked"),

    // This one exists?
    IRON_I              ("Iron I"),
    IRON_II             ("Iron II"),
    IRON_III            ("Iron III"),

    // Skipped the rank hehe.
    BRONZE_I            ("Bronze I"),
    BRONZE_II           ("Bronze II"),
    BRONZE_III          ("Bronze III"),

    // Was there. Good old times.
    SILVER_I            ("Silver I"),
    SILVER_II           ("Silver II"),
    SILVER_III          ("Silver III"),

    // I'm still hard-stuck there ;-;
    GOLD_I              ("Gold I"),
    GOLD_II             ("Gold II"),
    GOLD_III            ("Gold III"),

    // Sweaters.
    PLATINUM_I          ("Platinum I"),
    PLATINUM_II         ("Platinum II"),
    PLATINUM_III        ("Platinum III"),

    // Bigger sweaters.
    DIAMOND_I           ("Diamond I"),
    DIAMOND_II          ("Diamond II"),
    DIAMOND_III         ("Diamond III"),

    // HOW?
    ASCENDANT_I         ("Ascendant I"),
    ASCENDANT_II        ("Ascendant II"),
    ASCENDANT_III       ("Ascendant III"),

    // ARE YOU EVEN EMPLOYED?
    IMMORTAL_I          ("Immortal I"),
    IMMORTAL_II         ("Immortal II"),
    IMMORTAL_III        ("Immortal III"),

    // I think this is you job, right?
    RADIANT             ("Radiant");


    /**
     * Yes I know. I'm very professional. xd
     */


    private String tierId, tierColor;
    private final String tierName;

    Tier(final String tierName) {
        this.tierName = tierName;
    }

    /**
     * Returns the agent name.
     *
     * @return Agent name.
     */
    public String getTierName() {
        return this.tierName;
    }

    /**
     * Update tier color. Should only be called once
     * during runtime to set the agent id fetched from
     * the valorant-api.
     *
     * @param tierColor Tier color.
     */
    public void updateTierColor(final String tierColor) {
        if (this.tierColor != null) {
            throw new IllegalStateException("Tier color is already set!");
        }

        this.tierColor = tierColor;
    }

    /**
     * Get the tier color.
     *
     * @return Tier color.
     */
    public String getTierColor() {
        return tierColor;
    }

    /**
     * Update tier id. Should only be called once
     * during runtime to set the agent id fetched from
     * the valorant-api.
     *
     * @param tierId Tier id.
     */
    public void updateTierId(final String tierId) {
        if (this.tierId != null) {
            throw new IllegalStateException("Tier ID is already set!");
        }

        this.tierId = tierId;
    }

    /**
     * Get the tier id. This one needs to be set first after
     * fetching the agent uuid from the valorant-api.
     * It can then be used to easier navigate through
     * each agent's images and other information.
     *
     * @return Tier id.
     */
    public String getTierId() {
        if (this.tierId == null) {
            throw new IllegalStateException("Tier ID is not set yet!");
        }

        return this.tierId;
    }

    /**
     * Returns the DisplayImage of the tier.
     *
     * @return DisplayImage.
     */
    public DisplayImage getImage() {
        return VIT.get().getImageProvider().getTiers().getImage(this.tierId);
    }

    /**
     * Returns the mini sized DisplayImage of the tier.
     *
     * @return DisplayImage.
     */
    public DisplayImage getMiniImage() {
        return VIT.get().getImageProvider().getTiers().getMiniImage(this.tierId);
    }

    /**
     * Iterates through all tiers and compare their names
     * with the provided input. Returns the agent if it finds a match,
     * otherwise returns null.
     *
     * @param tierName Tier name. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Tier getTierByName(final String tierName) {
        for (final Tier tier : Tier.values()) {
            if (tier.getTierName().equalsIgnoreCase(tierName)) {
                return tier;
            }
        }

        return null;
    }

    /**
     * Iterates through all tiers and compare their ids
     * with the provided input. Returns the agent if it finds a match,
     * otherwise returns null.
     *
     * @param tierId Tier id. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Tier getTierById(final String tierId) {
        for (final Tier tier : Tier.values()) {
            if (tier.getTierId().equalsIgnoreCase(tierId)) {
                return tier;
            }
        }

        return null;
    }
}