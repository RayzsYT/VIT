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

    /**
     * Darkens a copy of a. image and returns the darkened
     * version of the image back.
     *
     * @param sourceImage  The original image
     * @param factor How strong to darken (Black = 0.0 --- 1.0 = Nothing)
     *
     * @return Darkened image
     */
    public static Image darkenImage(final Image sourceImage, final float factor) {

        final int height = sourceImage.getHeight(null);
        final int width = sourceImage.getWidth(null);

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

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                final int rgba = cpyImage.getRGB(x, y);

                final int alpha = (rgba >> 24) & 0xff;

                //Darkens each part based on the factor.
                final int red   = (int) (factor * ((rgba >> 16) & 0xff));
                final int green = (int) (factor * ((rgba >> 8) & 0xff));
                final int blue  = (int) (factor * (rgba & 0xff));

                // Set new darken color
                final int newColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                cpyImage.setRGB(x, y, newColor);
            }
        }

        return cpyImage;
    }

    /**
     * Scale an image to a certain size and still respect it's
     * previous size, so it won't be corrupted.
     *
     * @param image Source image.
     * @param maxWidth Target width.
     * @param maxHeight Target height.
     *
     * @return Rescaled image.
     */
    public static Image rescale(
            final Image image,
            final int maxWidth,
            final int maxHeight
    ) {
        final int originalWidth = image.getWidth(null);
        final int originalHeight = image.getHeight(null);

        final double widthScale = (double) maxWidth / originalWidth;
        final double heightScale = (double) maxHeight / originalHeight;

        final double scale = Math.min(widthScale, heightScale);

        final int newWidth = (int) (originalWidth * scale);
        final int newHeight = (int) (originalHeight * scale);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}