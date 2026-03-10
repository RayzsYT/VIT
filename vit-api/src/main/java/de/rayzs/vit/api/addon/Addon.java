package de.rayzs.vit.api.addon;

import de.rayzs.vit.api.VITAPI;

public class Addon {

    private final VITAPI api;
    private final AddonDescription description;

    private boolean enabled = true;

    public Addon(
            final VITAPI api,
            final AddonDescription description
    ) {
        this.api = api;
        this.description = description;
    }

    /**
     * What to do during bootup.
     */
    public void onEnable() {}

    /**
     * What to do during shutdown.
     */
    public void onDisable() {}

    /**
     * If addon is enabled. Please do NOT
     * override this method here!
     *
     * @return True if addon is enabled. False otherwise.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Addon description object containing
     * all necessary information about this
     * addon, like it's id, path to the main class,
     * who made it, and what purpose it serves.
     *
     * @return Addon description.
     */
    public AddonDescription getDescription() {
        return this.description;
    }
}
