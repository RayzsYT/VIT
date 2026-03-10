package de.rayzs.vit.api.addon;

import de.rayzs.vit.api.VITAPI;

public class Addon {

    private final VITAPI api;

    private final String addonName, addonVersion, addonPurpose;
    private final String[] addonAuthors;

    public Addon(
            final VITAPI api,
            final String addonName,
            final String addonVersion,
            final String addonPurpose,
            final String... addonAuthors
    ) {
        this.api = api;
        this.addonName = addonName;
        this.addonVersion = addonVersion;
        this.addonPurpose = addonPurpose;
        this.addonAuthors = addonAuthors;
    }

    /**
     * Get the addon name.
     *
     * @return Addon name.
     */
    public String getAddonName() {
        return this.addonName;
    }

    /**
     * Get purpose of the addon. Practically
     * just a short description on what this
     * addon does or for what it is for.
     *
     * @return Purpose of the addon.
     */
    public String getAddonPurpose() {
        return this.addonPurpose;
    }

    /**
     * Get version of the addon.
     *
     * @return Addon version.
     */
    public String getAddonVersion() {
        return this.addonVersion;
    }

    /**
     * Get an array of all contributors of
     * the addon.
     *
     * @return Contributors who wrote the addon.
     */
    public String[] getAddonAuthors() {
        return this.addonAuthors;
    }
}
