package de.rayzs.vit.api.image;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.utils.FileUtils;

import java.io.File;

public enum SystemImages {

    LOGO        ("images/logo.png"),
    ERROR       ("images/error.png"),
    WARNING     ("images/warning.png"),
    LOADING     ("images/loading.gif"),
    ICON        ("icon/icon.ico");


    private final DisplayImage displayImage;
    private final File imageFile;

    SystemImages(final String inFilePath) {
        // Export the file first if it doesn't exist.
        this.imageFile = FileUtils.exportResourceFile(
                inFilePath,
                FileDir.SYSTEM
        );

        final String[] fileNameSplit = this.imageFile.getName().split("\\."); // ["file", "png"]
        final String fileName = fileNameSplit[0];                         // "file"
        final String fileType = fileNameSplit[1];                         // "png"

        this.displayImage = new DisplayImage(
            null,
                FileDir.SYSTEM,
                fileName,
                fileType
        );
    }

    /**
     * Get associated DisplayImage.
     *
     * @return DisplayImage.
     */
    public DisplayImage getDisplayImage() {
        return this.displayImage;
    }

    /**
     * Get file of the image.
     *
     * @return Image file
     */
    public File getImageFile() {
        return imageFile;
    }
}
