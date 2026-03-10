package de.rayzs.vit.api.utils;

import de.rayzs.vit.api.file.FileDir;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {


    /**
     * Reads a resource file from inside the jar
     * and exports it to the directed file out path.
     *
     * @param inFilePath Resource file path.
     * @param outFileDir Export file directory.
     */
    public static File exportResourceFile(
            final String inFilePath,
            final FileDir outFileDir
    ) {

        final URL url = FileUtils.class.getClassLoader().getResource(
                inFilePath
        );

        if (url == null) {
            throw new NullPointerException("Resource not found: " + inFilePath);
        }


        try {

            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            final InputStream inputStream = connection.getInputStream();
            final File outputFile = outFileDir.getFile(
                    inFilePath.substring(inFilePath.lastIndexOf("/") + 1)
            );

            if (outputFile.exists()) {
                return outputFile;
            }

            final OutputStream outputStream = new FileOutputStream(outputFile);
            final byte[] buffer = new byte[1024];

            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            outputStream.close();
            inputStream.close();

            return outputFile;

        } catch (Exception exception) {
            throw new NullPointerException("Failed to export resource file: " + inFilePath);
        }
    }

    /**
     * Reads a resource file and returns it's input.
     *
     * @param inFilePath Resource file path.
     */
    public static String readResourceInput(final String inFilePath) {

        final URL url = FileUtils.class.getClassLoader().getResource(inFilePath);

        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + inFilePath);
        }

        try {
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            try (InputStream inputStream = connection.getInputStream()) {
                return new String(inputStream.readAllBytes());
            }

        } catch (Exception exception) {
            throw new RuntimeException("Failed to read resource file: " + inFilePath, exception);
        }
    }
}
