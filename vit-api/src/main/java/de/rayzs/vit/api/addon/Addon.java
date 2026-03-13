package de.rayzs.vit.api.addon;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.file.FileDir;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Addon {

    protected final VITAPI api;
    protected final JSONObject settings;

    private final AddonDescription description;

    private boolean enabled = true;

    public Addon(
            final VITAPI api,
            final AddonDescription description
    ) {
        this.api = api;
        this.description = description;

        final File configFolder = new File(description.getId());
        if (!configFolder.isDirectory()) {
            if (!configFolder.mkdir()) {
                throw new RuntimeException("Could not create config folder for addon '" + description.getId() + "'!");
            }
        }

        final File configFile = new File(configFolder, "config.json");


        if (!configFile.exists()) {

            try {
                configFile.createNewFile();
            } catch (IOException exception) {
                throw new RuntimeException("Failed to create config.json file for addon '" + description.getId() + "'!", exception);
            }

        }


        this.settings = new JSONObject();
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

    /**
     * Get settings JSONObject.
     *
     * @return Settings JSONObject.
     */
    public JSONObject getSettings() {
        return this.settings;
    }
}
