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

    private File imageFile;

    private ImageIcon icon;
    private Image image;

    private boolean isIcon = false, isGif = false;

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

        downloadElement = new DownloadElement(
                this.url,
                this.fileName
        );

        isIcon = fileType.equals("ico");
        isGif = fileType.equals("gif");

        imageFile = dir.getFile(this.fileName);
    }

    /**
     * Checks if the image is already saved or needs
     * to be downloaded first.
     *
     * @return True if downloaded and stored. False otherwise.
     */
    public boolean doesExist() {
        return imageFile != null && imageFile.exists();
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
     *
     * @return If image could be loaded or not.
     */
    public boolean updateImage() {
        imageFile = dir.getFile(fileName);

        if (isIcon) {
            return false;
        }

        if (imageFile.exists()) {
            try {

                if (isGif) {
                    this.icon = new ImageIcon(imageFile.getAbsolutePath());
                    this.image = icon.getImage();
                } else {
                    this.image = ImageIO.read(imageFile);
                    this.icon = new ImageIcon(image);
                }

                return true;
            } catch (Exception exception) {
                throw new RuntimeException("Could not load image from " + imageFile.getAbsolutePath());
            }
        }

        return false;
    }

    /**
     * Deallocate the image to save some
     * memory in case the image isn't required
     * anymore. After all, RAM is pricey and
     * very valuable in this day in age. ^^
     */
    public void deallocate() {
        if (icon != null) {
            final Image img = icon.getImage();
            if (img != null) img.flush();
        }

        if (image != null) {
            image.flush();
        }

        icon = null;
        image = null;
    }

    /**
     * Get the image as ImageIcon.
     *
     * @return ImageIcon.
     */
    public ImageIcon getIcon() {

        if (this.isIcon) {
            throw new IllegalStateException("This image is an .ico file and cannot be converted into an image icon.");
        }

        if (this.icon == null && !updateImage()) {
            throw new NullPointerException("Image icon does not exist! (url=" + this.url + ", filename=" + this.fileName + " )");
        }

        return this.icon;
    }

    /**
     * Get a rescaled version of the icon.
     *
     * @param width Rescaled icon width.
     * @param height Rescaled icon height.
     * @param hints Image#(HINTS)
     *
     * @return Rescaled icon.
     */
    public ImageIcon getIcon(
            final int width,
            final int height,
            final int hints
    ) {

        if (this.isIcon) {
            throw new IllegalStateException("This image is an .ico file and cannot be converted into an image icon.");
        }

        return new ImageIcon(getImage(width, height, hints));
    }

    /**
     * Get the image.
     *
     * @return Image.
     */
    public Image getImage() {

        if (this.isIcon) {
            throw new IllegalStateException("This image is an .ico file and cannot be converted into an image.");
        }

        if (this.image == null && !updateImage()) {
            throw new NullPointerException("Image does not exist! (url=" + this.url + ", filename=" + this.fileName + " )");
        }

        return this.image;
    }

    /**
     * Get a rescaled version of the image.
     *
     * @param width Rescaled image width.
     * @param height Rescaled image height.
     * @param hints Image#(HINTS)
     *
     * @return Rescaled image.
     */
    public Image getImage(
            final int width,
            final int height,
            final int hints
    ) {

        if (this.isIcon) {
            throw new IllegalStateException("This image is an .ico file and cannot be converted into an image.");
        }

        return this.getImage().getScaledInstance(width, height, hints);
    }

    @Override
    public String toString() {
        return "DisplayImage{" +
                "url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadElement=" + downloadElement +
                ", dir=" + dir +
                ", image=" + image +
                ", icon=" + icon +
                ", isIcon=" + isIcon +
                '}';
    }
}
