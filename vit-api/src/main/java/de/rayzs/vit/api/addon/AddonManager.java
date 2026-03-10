package de.rayzs.vit.api.addon;

import java.util.Set;

public interface AddonManager {

    /**
     * Load all addons.
     */
    void loadAddons();

    /**
     * Unload all addons.
     */
    void unloadAddons();

    /**
     * Calls 'onDisable()' method and
     * unregisters an addon.
     *
     * @param addon Addon to be unloaded.
     */
    void unloadAddon(Addon addon);

    /**
     * Register an addon based
     * on its file-name.
     *
     * @param fileName Name of the addon.
     * @return Returns Addon instance if successful. Otherwise, returns null.
     */
    Addon loadAddonByFile(String fileName);

    /**
     * Get an addon based on
     * the name.
     *
     * @param fileName Name of the addon.
     * @return Returns the addon if found. Otherwise, returns null.
     */
    Addon getAddonByName(String fileName);

    /**
     * Returns an immutable set of
     * all loaded addons.
     *
     * @return Set of all loaded addons.
     */
    Set<Addon> getLoadedAddons();

    /**
     * Checks if the addon
     * is enabled.
     *
     * @param addon The addon which might be enabled.
     * @return Return true if Addon is enabled.
     */
    boolean isEnabled(Addon addon);

    /**
     * Checks if the addon
     * is disabled.
     *
     * @param addon The addon which might be disabled.
     * @return Return true if Addon is disabled.
     */
    boolean isDisabled(Addon addon);

}
