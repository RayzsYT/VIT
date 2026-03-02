package de.rayzs.vit.impl.image;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.image.DisplayImage;
import de.rayzs.vit.api.image.ImageIdMap;
import de.rayzs.vit.api.image.ImageProvider;

import java.util.*;

public class ImageProviderImpl implements ImageProvider {

    private final Map<FileDir, Map<String, DisplayImage>> images = new HashMap<>();

    private final ImageIdMap weaponsIdMap = new ImageIdMap(this, FileDir.WEAPONS);
    private final ImageIdMap agentsIdMap = new ImageIdMap(this, FileDir.AGENTS);
    private final ImageIdMap mapsIdMap = new ImageIdMap(this, FileDir.MAPS);
    private final ImageIdMap tiersIdMap = new ImageIdMap(this, FileDir.TIERS);

    @Override
    public DisplayImage getOrCreateImage(
            final String url,
            final FileDir dir,
            final String fileName
    ) {
        return this.images.computeIfAbsent(dir, k -> new HashMap<>()).put(fileName,
                new DisplayImage(url, dir, fileName)
        );
    }

    @Override
    public Collection<DisplayImage> getImages(FileDir dir) {
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
        return null;
    }
}
