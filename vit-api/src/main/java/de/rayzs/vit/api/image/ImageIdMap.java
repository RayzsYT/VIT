package de.rayzs.vit.api.image;

import de.rayzs.vit.api.file.FileDir;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps UUIDs with names and images.
 * The idea is to use this class to simplify the process
 * of storing images of skins, agents, and maps.
 */
public class ImageIdMap {

    private final Map<String, String> names = new HashMap<>();
    private final Map<String, DisplayImage> images = new HashMap<>();

    private final ImageProvider provider;
    private final FileDir dir;

    public ImageIdMap(final ImageProvider provider, final FileDir dir) {
        this.provider = provider;
        this.dir = dir;
    }

    /**
     * Apply a name to an id.
     *
     * @param id Id.
     * @param name Name.
     */
    public void putName(
            final String id,
            final String name
    ) {
        names.put(id, name);
    }

    /**
     * Apply an icon image to an id.
     *
     * @param id Skin id.
     * @param iconUrl Skin icon url.
     */
    public void putImage(
            final String id,
            final String iconUrl
    ) {
        this.images.putIfAbsent(id, provider.getOrCreateImage(
                iconUrl,
                dir,
                id
        ));
    }

    /**
     * Get name of the associated id.
     *
     * @param id Id.
     * @return Name.
     */
    public String getName(final String id) {
        return this.names.get(id);
    }

    /**
     * Get name of the associated id.
     *
     * @param id Id.
     * @return DisplayImage.
     */
    public DisplayImage getImage(final String id) {
        return this.images.get(id);
    }
}
