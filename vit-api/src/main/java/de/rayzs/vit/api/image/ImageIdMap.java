package de.rayzs.vit.api.image;

import de.rayzs.vit.api.file.FileDir;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps ids with names and images.
 * The idea is to use this class to simplify the process
 * of storing images of skins, agents, and maps.
 */
public class ImageIdMap {

    private final Map<String, String> idNames = new HashMap<>();
    private final Map<String, String> names = new HashMap<>();
    private final Map<String, DisplayImage> images = new HashMap<>();
    private final Map<String, DisplayImage> miniImages = new HashMap<>();

    private final ImageProvider provider;
    private final FileDir normalDir, miniDir;

    public ImageIdMap(
            final ImageProvider provider,
            final FileDir normalDir,
            final FileDir miniDir
    ) {
        this.provider = provider;
        this.normalDir = normalDir;
        this.miniDir = miniDir;
    }

    /**
     * Get a collection of all ids.
     *
     * @return Collection of all ids.
     */
    public Collection<String> getIds() {
        return images.keySet();
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
        names.putIfAbsent(id, name);
        idNames.putIfAbsent(name, id);
    }

    /**
     * Apply an icon image to an id.
     *
     * @param id Id.
     * @param imageUrl imageUrl.
     */
    public void putImage(
            final String id,
            final String imageUrl
    ) {
        this.images.putIfAbsent(id, provider.getOrCreateImage(
                imageUrl,
                normalDir,
                id
        ));
    }

    /**
     * Apply an icon image to an id.
     *
     * @param id Id.
     * @param imageUrl Image url.
     */
    public void putMiniImage(
            final String id,
            final String imageUrl
    ) {
        if (this.miniDir == null) {
            throw new IllegalStateException("That ImageIdMap does not have a mini image area!");
        }

        this.miniImages.putIfAbsent(id, provider.getOrCreateImage(
                imageUrl,
                miniDir,
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
     * Get the by its associated name.
     *
     * @param name Name.
     * @return Id.
     */
    public String getIdByName(final String name) {
        return this.idNames.get(name);
    }

    /**
     * Get the DisplayImage of the associated id.
     *
     * @param id Id.
     * @return DisplayImage.
     */
    public DisplayImage getImage(final String id) {
        return this.images.get(id);
    }

    /**
     * Get the mini DisplayImage of the associated id.
     *
     * @param id Id.
     * @return DisplayImage.
     */
    public DisplayImage getMiniImage(final String id) {
        if (this.miniDir == null) {
            throw new IllegalStateException("That ImageIdMap does not have a mini image area!");
        }

        return this.miniImages.get(id);
    }
}
