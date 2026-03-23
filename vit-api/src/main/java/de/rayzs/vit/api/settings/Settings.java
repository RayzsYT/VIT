package de.rayzs.vit.api.settings;

import de.rayzs.vit.api.VIT;

import java.util.Locale;

/**
 * Serves the only purpose to store, load, and retrieve
 * all settings that should exist globally.
 */
public enum Settings {


    // Last window location (Main GUI)
    LAST_WINDOW_LOC_X             ("last-window-location.x",  0),
    LAST_WINDOW_LOC_Y             ("last-window-location.y",  0),


    // Scan
    SCAN_PLAYER_MATCHES_AMOUNT      ("scan.player-matches-amount",  5),
    SCAN_PLAYER_PARTIES             ("scan.scan-player-parties",    false);





    private final Object defaultObj;
    private final String path;

    Settings(final String path, final Object defaultObj) {
        this.defaultObj = defaultObj;
        this.path = path;

        if (VIT.get() == null) {
            throw new NullPointerException("VIT API hasn't been initialized yet! Therefor cannot read nor process settings! ");
        }

        // Just sets the value in case it hasn't been before.
        VIT.get().getSettings().getOrSet(path, defaultObj);
    }

    /**
     * Read and retrieve the object of
     * the setting.
     *
     * @return Value of setting.
     */
    public <T> T read() {
        return (T) VIT.get().getSettings().getOrSet(path, defaultObj);
    }

    /**
     * Update a setting and write it into the file
     * directly and saves the changes.
     *
     * @param value New value.
     */
    public void update(final Object value) {
        VIT.get().getSettings().setAndSave(path, value);
    }
}
