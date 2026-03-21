package de.rayzs.vit.api.database;

import de.rayzs.vit.api.file.FileDir;

public enum Database {

    SEEN_PLAYERS        ("seen_players");


    private final DatabaseHandler handler;

    Database (final String name) {
        this.handler = Databases.createHandler(
                null,
                name,
                FileDir.STORAGE,
                name
        );
    }

    /**
     * Get database.
     *
     * @return Database.
     */
    public DatabaseHandler get() {
        return this.handler;
    }
}
