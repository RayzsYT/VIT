package de.rayzs.vit.api.configuration;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.utils.StringUtils;
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

    public Configuration(final FileDir fileDir, final String fileName) {
        this(new File(fileDir.getFolder(), fileName));
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
    public JSONObject getJsonObject() {
        return this.json;
    }

    /**
     * Get an object at a path. If the object is not found
     * there, it will be set instead using the default value,
     * saves the change, and returns the default value.
     *
     * @param path Path.
     * @param defaultValue Default value in case nothing's found.
     *
     * @return Returns the value of the path or the default value.
     */
    public <T> T getOrSet(final String path, final T defaultValue) {
        final Object object = this.get(path);

        if (object == null) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return (T) object;
    }

    /**
     * Set a value and saves the file after wards.
     *
     * @param path Path where to set the value at.
     * @param value Value to set.
     */
    public void setAndSave(final String path, final Object value) {
        set(json, path, value);

        try {
            save();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Get String of path.
     *
     * @param path Path.
     *
     * @return String.
     */
    public String getString(final String path) {
        final Object value = this.json.get(path);

        if (value instanceof String str) {
            return str;
        }

        throw new RuntimeException("Value of " + path + " is not a String!");
    }

    /**
     * Get Integer of path.
     *
     * @param path Path.
     *
     * @return Integer.
     */
    public int getInt(final String path) {
        final Object value = this.json.get(path);

        if (value instanceof Number number) {
            return number.intValue();
        }

        throw new RuntimeException("Value of " + path + " is not an Integer!");
    }

    /**
     * Get Float of path.
     *
     * @param path Path.
     *
     * @return Float.
     */
    public float getFloat(final String path) {
        final Object value = this.json.get(path);

        if (value instanceof Number number) {
            return number.floatValue();
        }

        throw new RuntimeException("Value of " + path + " is not a Float!");
    }

    /**
     * Get Double of path.
     *
     * @param path Path.
     *
     * @return Double.
     */
    public double getDouble(final String path) {
        final Object value = this.json.get(path);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        throw new RuntimeException("Value of " + path + " is not a Double!");
    }

    /**
     * Recursively gets an object from the json if
     * the path for it or the object exists.
     *
     * @param path Current path.
     *
     * @return Found object at path. Returns NULL if the path does not exist or no object exist at the end of the path.
     */
    public Object get(final String path) {
        return get(json, path);
    }

    /**
     * Recursively gets an object from the json if
     * the path for it or the object exists.
     *
     * @param json Current json.
     * @param path Current path.
     *
     * @return Found object at path. Returns NULL if the path does not exist or no object exist at the end of the path.
     */
    private Object get(final JSONObject json, final String path) {
        final int index = StringUtils.searchIndex(".", path);

        if (index != -1) {
            final String nextPath = path.substring(index);
            final String pathName = path.substring(0, index - 1);

            if (!json.has(pathName)) {
                return null;
            }

            final JSONObject nextJson = json.getJSONObject(pathName);
            return get(nextJson, nextPath);
        }

        return json.has(path) ? json.get(path) : null;
    }

    /**
     * Set a value.
     *
     * @param path Path where to set the value at.
     * @param value Value to set.
     */
    public Configuration set(final String path, final Object value) {
        set(json, path, value);
        return this;
    }

    /**
     * Recursively adds an object to the json.
     *
     * @param json Current json.
     * @param path Current path.
     * @param value Value to set.
     */
    private void set(final JSONObject json, final String path, final Object value) {
        final int index = StringUtils.searchIndex(".", path);

        if (index != -1) {
            final String nextPath = path.substring(index);
            final String pathName = path.substring(0, index - 1);

            final JSONObject nextJson;

            if (json.has(pathName)) {
                nextJson = json.getJSONObject(pathName);
            } else {
                nextJson = new JSONObject();
                json.put(pathName, nextJson);
            }

            set(nextJson, nextPath, value);
            return;
        }

        json.put(path, value);
    }


    /**
     * Get config file.
     *
     * @return Config file.
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Updates both {@link Configuration#file} & {@link Configuration#json}
     * fields by re-reading the file input of {@link Configuration#file}.
     *
     * @throws IOException If the operation fails.
     */
    public void update() throws IOException {
        this.file = new File(this.absoluteFilePath);

        this.file.getParentFile().mkdirs();     // Create all directories
        this.file.createNewFile();              // In case it does not exist yet

        updateJsonObject();
    }

    /**
     * Save changes applied to {@link Configuration#json} by writing
     * them into {@link Configuration#file} directly.
     *
     * @throws IOException If the operation fails.
     */
    public void save() throws IOException {
        this.file.getParentFile().mkdirs();     // Create all directories
        this.file.createNewFile();              // In case it does not exist yet


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
