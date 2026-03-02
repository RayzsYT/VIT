package de.rayzs.vit.api;

import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.image.SystemImages;

public class VIT {

    private static VITAPI API = null;

    /**
     * Fetches the initialized API instance.
     * Will throw an NullPointerException in case the VIT API isn't
     * initialized yet!
     *
     * @return API instance.
     */
    public static VITAPI get() {

        if (API == null) {
            throw new NullPointerException("VIT API isn't initialized yet!");
        }

        return API;
    }

    /**
     * Sets the VIT API if not initialized yet.
     * Please refrain from using this method in case
     * you are someone just using the API. Just ensure
     * that your program runs only once the API has already
     * been initialized.
     *
     * @param api VIT API implementation.
     */
    public static void set(final VITAPI api) {

        if (API != null) {
            throw new IllegalArgumentException("VIT API is already initialized!");
        }


        // Called so they create the necessary folders and files.
        FileDir.values();
        SystemImages.values();


        API = api;
    }
}
