package de.rayzs.vit.api.download;

import de.rayzs.vit.api.file.FileDir;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class DownloadProcess {

    private final FileDir dir;
    private final DownloadElement[] elements;

    private boolean started = false;

    private int current;
    private final int max;

    private float percent = 0;

    public DownloadProcess(
            final FileDir dir,
            final DownloadElement[] elements
    ) {
        this.dir = dir;
        this.elements = elements;
        this.max = elements.length;
    }

    /**
     * Get current amount of downloaded files.
     *
     * @return Amount of downloaded files.
     */
    public int getDownloadedFiles() {
        return this.current;
    }

    /**
     * Get current amount of remaining files
     * to download.
     *
     * @return Amount of remaining files to download.
     */
    public int getRemainingFiles() {
        return this.max - this.current;
    }

    /**
     * Get percentage displaying how many
     * of the files have been downloaded
     * already.
     *
     * @return Percentage of downloaded files.
     */
    public float getPercent() {
        return this.percent;
    }

    /**
     * Indicates if the download process is completed or not.
     *
     * @return True if the download process is still running. False otherwise.
     */
    public boolean isCompleted() {
        return this.current == this.max;
    }

    /**
     * Start the download process.
     *
     * @param consumer Consumer after each downloaded file.
     */
    public void start(final Consumer<DownloadProcess> consumer) {
        if (started) {
            throw new IllegalStateException("Download process is already running!");
        }

        consumer.accept(this);
        downloadRecursively(consumer);
    }

    /**
     * Goes through each DownloadElement until
     * it has been gone through all of them.
     *
     * @param consumer Consumer after each downloaded file.
     */
    private void downloadRecursively(final Consumer<DownloadProcess> consumer) {
        if (isCompleted()) {
            this.percent = 100;
            return;
        }

        // Fetch current DownloadElement.
        final DownloadElement element = this.elements[this.current];
        final File file = dir.getFile(element.fileName());

        // Download file. Still counts it as completed even if the process failed.
        // Otherwise, the function will never end if something does not work out as
        // intended. I mean, I would rather have that instead of a never ending loop
        // because of some failed internet connection.
        try (InputStream in = new URL(element.url()).openStream()) {
            Files.copy(in, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        current++;
        percent = (100.0f / this.max) * this.current;

        consumer.accept(this);
        downloadRecursively(consumer);  // Loop
    }
}
