package de.rayzs.vit.api;

import de.rayzs.vit.api.image.ImageProvider;

public interface VITAPI {

    default String getVersion() { return "0.0.1"; }

    /**
     * Get the ImageProvider to create
     * or get DisplayImages.
     * 
     * @return ImageProvider.
     */
    ImageProvider getImageProvider();
}
