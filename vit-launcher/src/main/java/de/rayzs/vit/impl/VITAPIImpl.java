package de.rayzs.vit.impl;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.image.ImageProvider;
import de.rayzs.vit.impl.image.ImageProviderImpl;

public class VITAPIImpl implements VITAPI {

    private final ImageProvider imageProvider;

    public VITAPIImpl() {
        this.imageProvider = new ImageProviderImpl();
    }

    @Override
    public ImageProvider getImageProvider() {
        return this.imageProvider;
    }
}
