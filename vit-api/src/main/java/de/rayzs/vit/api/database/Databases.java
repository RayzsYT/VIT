package de.rayzs.vit.api.database;

import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.file.FileDir;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.function.Consumer;

public class Databases {

    private Databases() {}

    private static final HashMap<Addon, HashMap<String, DatabaseHandler>> databases = new HashMap<>();

    public static HashMap<String, DatabaseHandler> getDatabaseHandlers(Addon addon) {
        return databases.get(addon);
    }

    public static DatabaseHandler createHandler(
            final Addon addon,
            final String id,
            final String databaseUrl,
            final String databaseUser,
            final String databasePassword
    ) {


        final DatabaseHandler handler = new DefaultDatabaseHandler(
                databaseUrl, databaseUser, databasePassword
        );

        databases.computeIfAbsent(
                addon,
                k -> new HashMap<>()
        ).put(id, handler);


        return handler;
    }


    public static DatabaseHandler createHandler(
            final Addon addon,
            final String id,
            final FileDir fileDir,
            final String fileName
    ) {

        final File file = fileDir.getFile(fileName + ".db");

        try {
            file.createNewFile();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }


        final DatabaseHandler handler = new DefaultDatabaseHandler(file.getAbsolutePath());

        databases.computeIfAbsent(
                addon,
                k -> new HashMap<>()
        ).put(id, handler);


        return handler;
    }


    private static class DefaultDatabaseHandler implements DatabaseHandler {

        private Connection connection;
        private final String url, user, pass;

        private DefaultDatabaseHandler(String path) {
            this.url = "jdbc:sqlite:" + path;

            this.user = null;
            this.pass = null;
        }

        private DefaultDatabaseHandler(
                final String databaseUrl,
                final String databaseUser,
                final String databasePassword
        ) {

            this.url = "jdbc:mysql://" + databaseUrl;

            this.user = databaseUser;
            this.pass = databasePassword;
        }

        @Override
        public void prepare() {
            try {

                if (user == null || pass == null) {
                    connection = DriverManager.getConnection(url, user, pass);
                } else {
                    connection = DriverManager.getConnection(url);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void write(final String query) {
            try {
                connection.prepareStatement(query).execute();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void write(final String query, final Consumer<PreparedStatement> action) {
            try {
                action.accept(connection.prepareStatement(query));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public ResultSet readAndGet(final String query) {
            try {
                final PreparedStatement statement = connection.prepareStatement(query);

                return statement.executeQuery();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return null;
        }

        @Override
        public Object readAndGetObject(final String query) {
            ResultSet resultSet = readAndGet(query);

            if (resultSet == null) {
                return null;
            }

            Object obj = null;

            try {
                obj = resultSet.getObject(1);
                resultSet.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return obj;
        }

        @Override
        public int readAndGetInt(final String query) {
            ResultSet resultSet = readAndGet(query);
            int value = -1;

            if (resultSet == null) {
                return value;
            }

            try {
                value = resultSet.getInt(1);
                resultSet.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return value;
        }

        @Override
        public String readAndGetString(final String query) {
            ResultSet resultSet = readAndGet(query);

            if (resultSet == null) {
                return null;
            }

            String str = null;

            try {
                str = resultSet.getString(1);
                resultSet.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return str;
        }

        @Override
        public double readAndGetDouble(final String query) {
            ResultSet resultSet = readAndGet(query);
            double value = -1;

            if (resultSet == null) {
                return value;
            }

            try {
                value = resultSet.getDouble(1);
                resultSet.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return value;
        }
    }
}