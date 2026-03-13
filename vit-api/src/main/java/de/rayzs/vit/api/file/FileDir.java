package de.rayzs.vit.api.file;

import java.io.File;

public enum FileDir {

    APP_DATA            (null, System.getenv("LOCALAPPDATA")),

    VALORANT_CONF       (APP_DATA, "Riot Games/Riot Client/Config"),
    VALORANT_LOGS       (APP_DATA, "VALORANT/Saved/Logs"),

    ROOT                (APP_DATA, "VIT"),


    STORAGE             (ROOT,      "storage"),
    ADDONS              (ROOT,      "addons"),
    SCRIPTS             (ROOT,      "scripts"),
    LOGS                (ROOT,      "logs"),
    ASSETS              (ROOT,      "assets"),

    GAMES               (STORAGE,    "games"),

    SYSTEM              (ASSETS,    "system"),

    WEAPONS             (ASSETS,    "weapons"),
    AGENTS              (ASSETS,    "agents"),

    TIERS               (ASSETS,    "tiers"),
    TIERS_NORMAL        (TIERS,     "normal"),
    TIERS_SMALL         (TIERS,     "small"),

    MAPS                (ASSETS,    "maps"),
    MAPS_NORMAL         (MAPS,      "normal"),
    MAPS_SMALL          (MAPS,      "small");


    private final FileDir parent;
    private final String folderName;

    private final File folder;

    FileDir(final FileDir parent, final String folderName) {
        this.parent = parent;
        this.folderName = folderName;

        this.folder = parent != null
                ? new File(parent.getFolder(), folderName)
                : new File(folderName);

        if (this.folder.mkdir()) {
            System.out.println("Created folder: " + this.folder.getAbsolutePath());
        }
    }

    /**
     * Get folder as File object.
     *
     * @return Folder as File object.
     */
    public File getFolder() {
        return this.folder;
    }


    /**
     * Return file with the current FileDir as folder.
     *
     * @param fileName File name.
     * @return File.
     */
    public File getFile(final String fileName) {
        return new File(this.folder, fileName);
    }


    /**
     * Get parental FileDir enum.
     *
     * @return Parental FileDir enum.
     */
    public FileDir getParent() {
        return this.parent;
    }

    /**
     * Get current FileDir enum.
     *
     * @return FileDir enum.
     */
    public FileDir get() {
        return this;
    }

    /**
     * Get current folder name.
     *
     * @return Folder name.
     */
    public String getFolderName() {
        return this.folderName;
    }
}
