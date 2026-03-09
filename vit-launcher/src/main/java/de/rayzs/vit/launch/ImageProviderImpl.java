package de.rayzs.vit.launch;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.image.DisplayImage;
import de.rayzs.vit.api.image.ImageIdMap;
import de.rayzs.vit.api.image.ImageProvider;

import java.util.*;

public class ImageProviderImpl implements ImageProvider {

    private final Map<FileDir, Map<String, DisplayImage>> images = new HashMap<>();

    // Images that only have one image size.
    private final ImageIdMap weaponsIdMap = new ImageIdMap(this, FileDir.WEAPONS, null);
    private final ImageIdMap agentsIdMap = new ImageIdMap(this, FileDir.AGENTS, null);

    // Images that contain both normal and mini sized images.
    private final ImageIdMap mapsIdMap = new ImageIdMap(this, FileDir.MAPS_NORMAL, FileDir.MAPS_SMALL);
    private final ImageIdMap tiersIdMap = new ImageIdMap(this, FileDir.TIERS_NORMAL, FileDir.TIERS_SMALL);

    @Override
    public DisplayImage getOrCreateImage(
            final String url,
            final FileDir dir,
            final String fileName
    ) {
        this.images.computeIfAbsent(dir, k -> new HashMap<>());

        // Get the current image result.
        DisplayImage image = this.images.get(dir).get(fileName);
        if (image == null) {
            // Put it inside the map in case it does not exist yet.
            image = new DisplayImage(url, dir, fileName);

            this.images.get(dir).put(fileName, image);
            return image; // Returning just created image.
        }

        // Returning the found image.
        return image;
    }

    @Override
    public Collection<DisplayImage> getImages(final FileDir dir) {
        final Map<String, DisplayImage> map = this.images.get(dir);

        if (map == null) {
            return Collections.emptyList();
        }

        return map.values();
    }

    @Override
    public ImageIdMap getWeaponSkins() {
        return this.weaponsIdMap;
    }

    @Override
    public ImageIdMap getMaps() {
        return this.mapsIdMap;
    }

    @Override
    public ImageIdMap getAgents() {
        return this.agentsIdMap;
    }

    @Override
    public ImageIdMap getTiers() {
        return this.tiersIdMap;
    }
}
