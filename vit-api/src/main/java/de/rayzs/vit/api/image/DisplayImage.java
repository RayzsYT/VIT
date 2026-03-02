package de.rayzs.vit.api.image;

import de.rayzs.vit.api.download.DownloadElement;
import de.rayzs.vit.api.file.FileDir;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DisplayImage {

    private final String url, fileName;

    private final DownloadElement downloadElement;
    private final FileDir dir;

    private Image image;
    private ImageIcon icon;

    public DisplayImage(
            final String url,
            final FileDir dir,
            final String fileName
    ) {
        this(url, dir, fileName, "png");
    }

    public DisplayImage(
            final String url,
            final FileDir dir,
            final String fileName,
            final String fileType
    ) {
        this.url = url;
        this.dir = dir;
        this.fileName = fileName + "." + fileType;

        this.downloadElement = new DownloadElement(
                this.url,
                this.fileName
        );

        updateImage();
    }

    /**
     * Checks if the image is already saved or needs
     * to be downloaded first.
     *
     * @return True if downloaded and stored. False otherwise.
     */
    public boolean doesExist() {
        return this.image != null;
    }

    /**
     * Get FileDir where image is stored.
     *
     * @return FileDir.
     */
    public FileDir getDir() {
        return this.dir;
    }

    /**
     * Return DownloadElement from image.
     *
     * @return DownloadElement.
     */
    public DownloadElement getDownloadElement() {
        return this.downloadElement;
    }

    /**
     * Updates the image in case it did
     * not load at first.
     */
    public void updateImage() {
        final File imageFile = dir.getFile(fileName);

        if (imageFile.exists()) {
            try {
                this.image = ImageIO.read(imageFile);
                this.icon = new ImageIcon(this.image);
            } catch (Exception exception) {
                throw new RuntimeException("Could not load image from " + imageFile.getAbsolutePath());
            }
        }
    }

    public ImageIcon getIcon() {
        if (this.icon == null) {
            throw new NullPointerException("Image icon does not exist! (url=" + this.url + ", filename=" + this.fileName + " )");
        }

        return this.icon;
    }

    public Image getImage() {
        if (this.image == null) {
            throw new NullPointerException("Image does not exist! (url=" + this.url + ", filename=" + this.fileName + " )");
        }

        return this.image;
    }
}
