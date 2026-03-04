package de.rayzs.vit.api.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    /**
     * Crops a copy of an image to a specified
     * size around its center.
     *
     * @param sourceImage Image to crop.
     * @param targetWidth Target size of image.
     * @param targetHeight Target height of image.
     *
     * @return Cropped image.
     */
    public static Image cropImage(
            final Image sourceImage,
            final int targetWidth,
            final int targetHeight
    ) {

        // Creates an empty image with the same size as the original.
        final BufferedImage cpyImage = new BufferedImage(
                sourceImage.getWidth(null),
                sourceImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        // Draws source image onto the copy.
        final Graphics graphics = cpyImage.getGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.dispose();


        final double targetRatio = (double) targetWidth / targetHeight;
        final int sourceWidth = cpyImage.getWidth();
        final int sourceHeight = cpyImage.getHeight();

        final double srcRatio = (double) sourceWidth / sourceHeight;

        int cropWidth = sourceWidth;
        int cropHeight = sourceHeight;


        if (srcRatio > targetRatio) {
            cropWidth = (int) (sourceHeight * targetRatio);
        } else {
            cropHeight = (int) (sourceWidth / targetRatio);
        }

        final int x = (sourceWidth - cropWidth) / 2;
        final int y = (sourceHeight - cropHeight) / 2;


        final BufferedImage croppedImage = cpyImage.getSubimage(
                x, y, cropWidth, cropHeight
        );


        return croppedImage.getScaledInstance(
                targetWidth,
                targetHeight,
                Image.SCALE_SMOOTH
        );
    }
}
