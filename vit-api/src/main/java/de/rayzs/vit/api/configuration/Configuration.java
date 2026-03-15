package de.rayzs.vit.api.configuration;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Configuration class to manage, save, and update settings
 * inside a json file.
 */
public class Configuration {

    private final String absoluteFilePath;

    private File file;
    private JSONObject json;

    public Configuration(final String absoluteFilePath) {
        this(new File(absoluteFilePath));
    }


    public Configuration(final File directory, final String fileName) {
        this(new File(directory, fileName));
    }

    public Configuration(final File file) {
        this.absoluteFilePath = file.getAbsolutePath();
        this.file = file;

        try {
            updateJsonObject();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * Get JSONObject of the configuration file.
     *
     * @return JSONObject.
     */
    public JSONObject getJson() {
        return this.json;
    }

    /**
     * Updates both {@link Configuration#file} & {@link Configuration#json}
     * fields by re-reading the file input of {@link Configuration#file}.
     *
     * @throws IOException If the operation fails.
     */
    public void update() throws IOException {
        this.file = new File(this.absoluteFilePath);
        this.file.createNewFile(); // In case it does not exist yet

        updateJsonObject();
    }

    /**
     * Save changes applied to {@link Configuration#json} by writing
     * them into {@link Configuration#file} directly.
     *
     * @throws IOException If the operation fails.
     */
    public void save() throws IOException {
        this.file.createNewFile(); // In case it does not exist yet


        final FileWriter writer = new FileWriter(this.file);

        writer.write(json.toString(2));
        writer.flush();
        writer.close();
    }

    /**
     * Only updates {@link Configuration#json}.
     *
     * @throws IOException If the operation fails.
     */
    private void updateJsonObject() throws IOException {
        if (!this.file.exists()) {
            this.json = new JSONObject();
            return;
        }

        this.json = new JSONObject(String.join(
                "", Files.readAllLines(this.file.toPath())
        ));
    }
}
