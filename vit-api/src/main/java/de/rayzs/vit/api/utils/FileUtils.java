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
     * @param outFileName Export file name.
     *
     * @throws IOException In case something went wrong.
     */
    public static void exportResourceFile(
            final String inFilePath,
            final FileDir outFileDir,
            final String outFileName
    ) throws IOException {

        final URL url = FileUtils.class.getClassLoader().getResource(
                inFilePath.replace("/", "\\")
        );

        if (url == null) {
            throw new NullPointerException("Resource not found: " + inFilePath);
        }

        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        final InputStream inputStream = connection.getInputStream();
        final File outputFile = outFileDir.getFile(outFileName);

        if (outputFile.exists()) {
            return;
        }

        try {
            final OutputStream outputStream = new FileOutputStream(outputFile);
            final byte[] buffer = new byte[1024];

            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            outputStream.close();
            inputStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
