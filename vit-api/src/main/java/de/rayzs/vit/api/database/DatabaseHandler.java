package de.rayzs.vit.api.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public interface DatabaseHandler {

    /**
     * Initialize and setup database.
     */
    void prepare();

    /**
     * Write data.
     *
     * @param query Query.
     *
     * <pre>
     * {@code
     *  write("SELECT ...");
     * }
     * </pre>
     */
    void write(
            final String query
    );

    /**
     * Write data.
     *
     * @param query Selection query in sql.
     * @param action Action to be taken.
     *
     * <pre>
     * {@code
     *  write(
     *      "SELECT ...",
     *      statement -> {
     *          statement.setString(1, ...);
     *          // ...
     *      }
     *  );
     * }
     * </pre>
     */
    void write(
            final String query,
            final Consumer<PreparedStatement> action
    );

    /**
     * Read and returns data.
     *
     * @param query Selection query in sql
     * @return Returns result in ResultSet.
     */
    ResultSet readAndGet(
            final String query
    );

    /**
     * Read and returns data.
     *
     * @param query Selection query in sql
     * @return Returns result as Object.
     */
    Object readAndGetObject(
            final String query
    );

    /**
     * Read and returns data.
     *
     * @param query Selection query in sql
     * @return Returns result in type String.
     */
    int readAndGetInt(
            final String query
    );

    /**
     * Read and returns data.
     *
     * @param query Selection query in sql
     * @return Returns result in type String.
     */
    String readAndGetString(
            final String query
    );

    /**
     * Read and returns data.
     *
     * @param query Selection query in sql
     * @return Returns result in type Double.
     */
    double readAndGetDouble(
            final String query
    );
}