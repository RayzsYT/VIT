package de.rayzs.vit.api.addon;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.utils.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

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
     * Does not overwrite or replace an already existing config file
     * and only tries to read it as a JSONObject.
     */
    protected void loadDefaultConfig() {
        final String configFileName = "config.json";


        File configFile = new File(getAddonDir(), configFileName);

        if (!configFile.exists()) {
            configFile = FileUtils.exportResourceFile(this.getClass(), configFileName, addonDir);
        }


        this.configFile = configFile;


        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stringBuilder.append(
                    String.join("", Files.readAllLines(configFile.toPath()))
            );
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        this.config = new JSONObject(stringBuilder.toString());
    }

    /**
     * Save current config changes.
     */
    public void saveConfig() {

        if (this.configFile == null) {
            throw new NullPointerException("Config couldn't be saved since there's none! Please use 'Addon#loadConfig()' first!");
        }


        try {
            final FileWriter writer = new FileWriter(this.configFile);

            writer.write(this.config.toString(2));
            writer.flush();
            writer.close();

            System.out.println("Successfully updated json object to file!");

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get default config JSONObject if loaded.
     * If not loaded yet, call {@link #loadConfig()} first.
     *
     * @return Settings JSONObject.
     */
    public JSONObject getConfig() {

        if (this.config == null) {
            throw new NullPointerException("Config not found! Please ensure to call this method here after you called 'Addon#loadConfig()' first.");
        }

        return this.config;
    }
}
