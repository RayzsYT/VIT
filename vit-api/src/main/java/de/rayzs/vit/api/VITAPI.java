package de.rayzs.vit.api;

import de.rayzs.vit.api.image.ImageProvider;

public interface VITAPI {

    /**
     * Get the ImageProvider to create
     * or get DisplayImages.
     * 
     * @return ImageProvider.
     */
    ImageProvider getImageProvider();
}
