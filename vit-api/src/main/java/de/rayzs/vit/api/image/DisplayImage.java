package de.rayzs.vit.api.image;

import java.awt.*;

public class DisplayImage {

    private final String url, fileName;
    private Image image;

    public DisplayImage(
            final String url,
            final String fileName
    ) {
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * Checks if the image is already saved or needs
     * to be downloaded first.
     *
     * @return True if downloaded and stored. False otherwise.
     */
    public boolean doesExist() {
        return image != null;
    }

    public void download() {

    }

    public Image getImage() {
        if (this.image == null) {
            throw new NullPointerException("Image does not exist! (url=" + this.url + ", filename=" + this.fileName + " )");
        }

        return this.image;
    }
}
