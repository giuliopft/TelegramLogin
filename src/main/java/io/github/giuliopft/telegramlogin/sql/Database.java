package io.github.giuliopft.telegramlogin.sql;

import io.github.giuliopft.telegramlogin.TelegramLogin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class Database {
    private final TelegramLogin telegramLogin;
    private final String path;

    public Database(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
        this.path = "jdbc:sqlite:" + telegramLogin.getDataFolder() + File.separator + "data.db";
    }

    public void initialize() throws SQLException, IOException {
        //noinspection ResultOfMethodCallIgnored
        new File(path.replace("jdbc:sqlite:", "")).createNewFile();
        try (Connection connection = DriverManager.getConnection(path)) {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Players" +
                    "(uuid TEXT NOT NULL PRIMARY KEY," +
                    "id INT);");
            telegramLogin.debug("data.db initialized");
        }
    }

    public ResultSet get(UUID uuid) throws SQLException {
        try (Connection connection = DriverManager.getConnection(path)) {
            String query = "SELECT * FROM Players WHERE uuid = " + uuid + ";";
            telegramLogin.debug("The following query is being executed: " + query);
            return connection.createStatement().executeQuery(query);
        }
    }

    public ResultSet get(int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(path)) {
            String query = "SELECT * FROM Players WHERE id = " + id + ";";
            telegramLogin.debug("The following query is being executed: " + query);
            return connection.createStatement().executeQuery(query);
        }
    }

    public void add(UUID uuid, int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(path)) {
            String update = "INSERT INTO Players VALUES (" + uuid + ", " + id + ");";
            telegramLogin.debug("The following update is being executed: " + update);
            connection.createStatement().executeUpdate(update);
        }
    }
}