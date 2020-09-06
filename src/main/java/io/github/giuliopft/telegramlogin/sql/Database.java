package io.github.giuliopft.telegramlogin.sql;

import io.github.giuliopft.telegramlogin.TelegramLogin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Database {
    private final TelegramLogin telegramLogin;
    private final String path;
    private final boolean mysql;
    private final String user;
    private final String password;

    public Database(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
        this.mysql = telegramLogin.getConfig().getBoolean("database.mysql");
        if (mysql) {
            this.path = "jdbc:mysql://" + telegramLogin.getConfig().getString("database.address") + ":" +
                    telegramLogin.getConfig().getInt("database.port") + "/telegramlogin";
            this.user = telegramLogin.getConfig().getString("database.user");
            this.password = telegramLogin.getConfig().getString("database.password");
            telegramLogin.debug("Using MySQL, address " + telegramLogin.getConfig().getString("database.address") +
                    ", port " + telegramLogin.getConfig().getInt("database.port") + ", user " + user + ", password " + password);
        } else {
            this.path = "jdbc:sqlite:" + telegramLogin.getDataFolder() + File.separator + "data.db";
            this.user = null;
            this.password = null;
            telegramLogin.debug("Using SQLite");
        }
    }

    public void initialize() throws SQLException, IOException {
        String tableUpdate = "CREATE TABLE IF NOT EXISTS Players" +
                "(uuid TEXT NOT NULL PRIMARY KEY," +
                "id INT);";
        if (!mysql) {
            //noinspection ResultOfMethodCallIgnored
            new File(path.replace("jdbc:sqlite:", "")).createNewFile();
            try (Connection connection = getConnection()) {
                connection.createStatement().executeUpdate(tableUpdate);
                telegramLogin.debug("data.db initialized");
            }
        } else {
            try (Connection connection = DriverManager.getConnection(path.replace("/telegramlogin", ""), user, password)) {
                connection.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS telegramlogin;");
                telegramLogin.debug("MySQL telegramlogin database initialized");
            }
            try (Connection connection = getConnection()) {
                connection.createStatement().executeUpdate(tableUpdate);
                telegramLogin.debug("Users table initialized");
            }
        }
    }

    public int get(UUID uuid) throws SQLException {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM Players WHERE uuid = \"" + uuid.toString() + "\";";
            telegramLogin.debug("The following query is being executed: " + query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return resultSet.next() ? resultSet.getInt("id") : 0;
        }
    }

    public Set<UUID> get(long id) throws SQLException {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM Players WHERE id = " + id + ";";
            telegramLogin.debug("The following query is being executed: " + query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            Set<UUID> set = new HashSet<>();
            while (resultSet.next()) {
                set.add(UUID.fromString(resultSet.getString("uuid")));
            }
            return set;
        }
    }

    public void add(UUID uuid, long id) throws SQLException {
        try (Connection connection = getConnection()) {
            String update = "INSERT INTO Players VALUES (\"" + uuid.toString() + "\", " + id + ");";
            telegramLogin.debug("The following update is being executed: " + update);
            connection.createStatement().executeUpdate(update);
        }
    }

    private Connection getConnection() throws SQLException {
        return mysql ? DriverManager.getConnection(path, user, password) : DriverManager.getConnection(path);
    }
}
