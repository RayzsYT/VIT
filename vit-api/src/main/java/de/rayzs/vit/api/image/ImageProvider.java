package de.rayzs.vit.api.image;

import de.rayzs.vit.api.file.FileDir;
import java.util.Collection;

public interface ImageProvider {

    /**
     * Get an already existing or create
     * a new DisplayImage object.
     *
     * @param url URL where to download the image.
     * @param dir FileDir where to store the file.
     * @param fileName File name.
     * @return DisplayImage.
     */
    DisplayImage getOrCreateImage(
            final String url,
            final FileDir dir,
            final String fileName
    );

    /**
     * Get all DisplayImages
     * from the FileDir.
     *
     * @param dir FileDir.
     * @return Collection of all DisplayImages.
     */
    Collection<DisplayImage> getImages(final FileDir dir);
}
