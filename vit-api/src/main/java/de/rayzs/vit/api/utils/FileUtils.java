package de.rayzs.vit.api.utils;

import de.rayzs.vit.api.file.FileDir;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {


    /**
     * Reads a resource file from inside the jar
     * and exports it to the out file directory.
     *
     * @param clazz Class of the project whose local resource folder should be chosen. NULL uses this class here instead.
     * @param inFilePath Resource file path.
     * @param outFileDir Export file directory.
     */
    public static File exportResourceFile(
            final Class<?> clazz,
            final String inFilePath,
            final FileDir outFileDir
    ) {
        return exportResourceFile(clazz, inFilePath, outFileDir.getFolder());
    }

    /**
     * Reads a resource file from inside the jar
     * and exports it to the out file directory.
     *
     * @param clazz Class of the project whose local resource folder should be chosen. NULL uses this class here instead.
     * @param inFilePath Resource file path.
     * @param outFileDir Export file directory.
     */
    public static File exportResourceFile(
            final Class<?> clazz,
            final String inFilePath,
            final File outFileDir
    ) {

        final URL url = (clazz != null ? clazz : FileUtils.class).getClassLoader().getResource(
                inFilePath
        );

        if (url == null) {
            throw new NullPointerException("Resource not found: " + inFilePath);
        }


        try {

            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            final InputStream inputStream = connection.getInputStream();
            final File outputFile = new File(
                    outFileDir,
                    inFilePath.substring(inFilePath.lastIndexOf("/") + 1)
            );

            if (outputFile.exists()) {
                return outputFile;
            }

            // Create directory if they don't exist yet
            outputFile.getParentFile().mkdirs();

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

    /**
     * Zips a txt file and names the zipped file
     * like the source file, just ending with .zip,
     * and also deletes the original file once created.
     *
     * @param file Log file
     * @return Zipped file with successful. NULL otherwise.
     */
    public static File zipFile(final File file) {

        if (!file.isFile() || !file.getName().endsWith(".txt") && !file.getName().endsWith(".o")) {
            throw new IllegalArgumentException("Only .txt & .o files can be zipped! Nothing more. Just a security measurement, since I don't trust myself.");
        }

        try {

            final File zippedFile = new File(file.getParentFile(), file.getName() + ".zip");

            final FileOutputStream fileOutputStream = new FileOutputStream(zippedFile);
            final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            final FileInputStream fileInputStream = new FileInputStream(file);
            final ZipEntry zipEntry = new ZipEntry(file.getName());

            zipOutputStream.putNextEntry(zipEntry);
            final byte[] bytes = new byte[1024];

            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }

            zipOutputStream.close();
            fileInputStream.close();
            fileOutputStream.close();

            if (!file.delete()) {
                System.err.println("Failed to delete source of zipped file: " + file.getAbsolutePath());
            }

            return zippedFile;

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
