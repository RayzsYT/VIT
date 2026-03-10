package de.rayzs.vit.api.addon;

import org.json.JSONObject;

public class AddonDescription {

    private final String id, version, author, purpose, website, main;

    public AddonDescription(final JSONObject json) {
        if (!json.has("id")) {
            throw new IllegalArgumentException("AddonDescription is missing an ID!");
        } else id = json.getString("id");

        if (!json.has("version")) {
            throw new IllegalArgumentException("AddonDescription is missing the version!");
        } else this.version = json.getString("version");

        if (!json.has("author")) {
            throw new IllegalArgumentException("AddonDescription is missing the author!");
        } else this.author = json.getString("author");

        if (!json.has("main")) {
            throw new IllegalArgumentException("AddonDescription is missing the path to the main class!");
        } else this.main = json.getString("main");


        website = json.has("website") ? json.getString("website") : null;
        purpose = json.has("purpose") ? json.getString("purpose") : null;
    }

    /**
     * ID or name of the addon. Must be
     * unique and cannot exist twice!
     *
     * @return Addon id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Version of the addon.
     *
     * @return Addon version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get website of the addon, if one provided.
     *
     * @return Addon website or null if not set.
     */
    public String getWebsite() {
        return this.website;
    }

    /**
     * Authors of the addon.
     *
     * @return Author of the addon.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Purpose of the addon. Basically just a
     * short explanation on what this addon does
     * and what it was made for.
     *
     * @return Purpose of the addon.
     */
    public String getPurpose() {
        return this.purpose;
    }

    /**
     * Path to the main class.
     *
     * @return Path to the main class.
     */
    public String getMain() {
        return this.main;
    }
}
