package de.rayzs.vit.api.objects.game;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.session.SessionState;

import java.io.*;

public record Game(
        Player self,            // Self player
        SessionState state,     // State
        Player[] players,       // Players
        String mapId,           // ID of map
        String server           // Connected server
) {


    /**
     * Stored the match into a file.
     *
     * @param game Match to store.
     *
     * @return True if match could be stored successfully. False otherwise.
     */
    public static boolean saveMatch(final Game game) {
        final File saveFile = FileDir.GAMES.getFile(VITAPI.DATE_FORMAT.format(System.currentTimeMillis()) + ".o");

        try {
            final FileOutputStream outputStream = new FileOutputStream(saveFile);
            final ObjectOutputStream write = new ObjectOutputStream(outputStream);

            write.writeObject(game);
            write.close();

            return true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * Loads a stored match from a file.
     * If successful, it will return it as a {@link Game} object.
     * If it failed, it will return NULL instead.
     *
     * @param saveFile File to read the game match from.
     * @return {@link Game} is successful. NULL otherwise.
     */
    public static Game loadMatch(final File saveFile) {
        try {
            final FileInputStream inputStream = new FileInputStream(saveFile);
            final ObjectInputStream read = new ObjectInputStream(inputStream);

            if (read.readObject() instanceof Game game) {
                read.close();

                return game;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
