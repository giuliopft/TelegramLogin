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

/**
 * Utility class for SQL queries and updates.
 */
public final class Database {
    /**
     * Main class instance.
     */
    private final TelegramLogin telegramLogin;
    /**
     * SQLite file path or MySQL address.
     */
    private final String path;
    /**
     * True if the database is running on MySQL, false if on SQLite.
     */
    private final boolean mysql;
    /**
     * MySQL only. MySQL username.
     */
    private final String user;
    /**
     * MySQL only. MySQL password.
     */
    private final String password;

    /**
     * Creates a database instance, fetching {@link #path}, {@link #mysql}, {@link #user} and {@link #password} from <code>config.yml</code>.
     * You should not need to call this constructor.
     *
     * @param telegramLogin Main class instance.
     * @see TelegramLogin#getDatabase()
     */
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

    /**
     * This method should be called asynchronously.
     * Creates a new database if it doesn't exist yet with an empty table inside.
     *
     * @throws SQLException Thrown if the database or the table cannot be created.
     * @throws IOException  SQLite only. Thrown if data.db cannot be created.
     */
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

    /**
     * This method should be called asynchronously.
     * Gets the Telegram ID associated with a player.
     *
     * @param uuid The player's UUID.
     * @return The associated Telegram ID, or 0 if the player is not in the database.
     * @throws SQLException Thrown if the query cannot be executed.
     */
    public int get(UUID uuid) throws SQLException {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM Players WHERE uuid = \"" + uuid.toString() + "\";";
            telegramLogin.debug("The following query is being executed: " + query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return resultSet.next() ? resultSet.getInt("id") : 0;
        }
    }

    /**
     * This method should be called asynchronously.
     * Gets a {@link Set}<{@link UUID}> containing all the UUIDs associated with a given Telegram ID.
     *
     * @param id The Telegram ID.
     * @return A {@link Set}<{@link UUID}> containing all the UUIDs associated with that Telegram ID.
     * @throws SQLException Thrown if the query cannot be executed.
     */
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

    /**
     * This method should be called asynchronously.
     * Adds a player to the database.
     *
     * @param uuid The player's UUID.
     * @param id   The Telegram ID to associate with the player.
     * @throws SQLException Thrown if the update cannot be executed.
     */
    public void add(UUID uuid, long id) throws SQLException {
        try (Connection connection = getConnection()) {
            String update = "INSERT INTO Players VALUES (\"" + uuid.toString() + "\", " + id + ");";
            telegramLogin.debug("The following update is being executed: " + update);
            connection.createStatement().executeUpdate(update);
        }
    }

    /**
     * Gets the correct {@link Connection}.
     *
     * @return The correct SQL {@link Connection} (MySQL or SQLite).
     * @throws SQLException Thrown if the connection cannot be established.
     */
    private Connection getConnection() throws SQLException {
        return mysql ? DriverManager.getConnection(path, user, password) : DriverManager.getConnection(path);
    }
}
