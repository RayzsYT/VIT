package de.rayzs.vit.api.addon;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class Addon {

    protected final VITAPI api;

    private final AddonDescription description;

    // Addon dir where all addon related files are stored
    protected final File addonDir;

    // Default config
    protected final Configuration config;

    private boolean enabled = true;

    public Addon(
            final VITAPI api,
            final AddonDescription description
    ) {
        this.api = api;
        this.description = description;

        this.addonDir = FileDir.ADDONS.getFile(description.getId());
        this.config = new Configuration(addonDir, "config.json");
    }

    /**
     * What to do during boot-up.
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

    /**
     * Get addon directory for all its config files.
     * If it does not exist yet, it will be created
     * once this method or {@link Addon#getConfig()}
     * is called.
     *
     * @return Addon directory.
     */
    public File getAddonDir() {

        if (!addonDir.isDirectory()) {
            if (!addonDir.mkdir()) {
                throw new RuntimeException("Could not create config folder for addon '" + description.getId() + "'!");
            }
        }

        return this.addonDir;
    }

    /**
     * Exports and loads the default config.json file from the
     * inner resource path to the {@link Addon#addonDir} folder.
     * This does not overwrite or replace an already existing config file!
     */
    protected void loadDefaultConfig() {
        if (!config.getFile().exists()) {
            config.getFile().getParentFile().mkdirs();

            FileUtils.exportResourceFile(
                    this.getClass(),
                    config.getFile().getName(),
                    addonDir
            );
        }


        try {
            config.update();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Save current config changes.
     */
    public void saveConfig() {
        try {
            config.save();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get default addon config.
     *
     * @return Default addon config.
     */
    public Configuration getConfig() {
        return config;
    }
}
