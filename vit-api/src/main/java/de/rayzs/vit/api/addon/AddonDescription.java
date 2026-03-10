package de.rayzs.vit.api.addon;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddonDescription {

    private final String id, version, author, purpose, main;

    public AddonDescription(final JSONObject json) {
        if (!json.has("id")) {
            throw new IllegalArgumentException("AddonDescription is missing an ID!");
        }

        if (!json.has("version")) {
            throw new IllegalArgumentException("AddonDescription is missing the version!");
        }

        if (!json.has("main")) {
            throw new IllegalArgumentException("AddonDescription is missing the path to the main class!");
        }


        id = json.getString("id");
        version = json.getString("version");
        author = json.getString("author");
        purpose = json.getString("purpose");
        main = json.getString("main");
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
