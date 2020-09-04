package io.github.giuliopft.telegramlogin;

import io.github.giuliopft.telegramlogin.bot.Bot;
import io.github.giuliopft.telegramlogin.listeners.login.LoginConfirmationListener;
import io.github.giuliopft.telegramlogin.listeners.login.PlayerJoinListener;
import io.github.giuliopft.telegramlogin.listeners.login.PlayerQuitListener;
import io.github.giuliopft.telegramlogin.listeners.login.PlayerRegisterListener;
import io.github.giuliopft.telegramlogin.listeners.security.block.BlockBreakListener;
import io.github.giuliopft.telegramlogin.listeners.security.block.BlockDamageListener;
import io.github.giuliopft.telegramlogin.listeners.security.block.BlockFertilizeListener;
import io.github.giuliopft.telegramlogin.listeners.security.block.BlockPlaceListener;
import io.github.giuliopft.telegramlogin.listeners.security.block.SignChangeListener;
import io.github.giuliopft.telegramlogin.listeners.security.enchantment.EnchantItemListener;
import io.github.giuliopft.telegramlogin.listeners.security.enchantment.PrepareItemEnchantListener;
import io.github.giuliopft.telegramlogin.listeners.security.entity.EntityPickupItemListener;
import io.github.giuliopft.telegramlogin.listeners.security.entity.EntityTameListener;
import io.github.giuliopft.telegramlogin.listeners.security.entity.PlayerLeashEntityListener;
import io.github.giuliopft.telegramlogin.listeners.security.entity.ProjectileLaunchListener;
import io.github.giuliopft.telegramlogin.listeners.security.hanging.HangingBreakByEntityListener;
import io.github.giuliopft.telegramlogin.listeners.security.hanging.HangingPlaceListener;
import io.github.giuliopft.telegramlogin.listeners.security.inventory.InventoryInteractListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.AsyncPlayerChatListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerBedEnterListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerBucketEmptyListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerBucketFillListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerCommandPreprocessListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerDropItemListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerEditBookListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerEggThrowListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerFishListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerInteractEntityListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerInteractListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerItemConsumeListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerItemMendListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerMoveListener;
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerShearEntityListener;
import io.github.giuliopft.telegramlogin.sql.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TelegramLogin is a plugin for Bukkit which forces players to authenticate
 * with Telegram before being able to play on a Minecraft server.
 *
 * @author giuliopft
 * @version 1.0
 */
public final class TelegramLogin extends JavaPlugin {
    /**
     * The Telegram bot which users should interact with.
     */
    private Bot bot;
    /**
     * The SQLite database where players and Telegram IDs are stored.
     */
    private final Database database = new Database(this);
    /**
     * Toggles debug statements.
     */
    private boolean debug;
    /**
     * The .yml locale, already parsed.
     */
    private FileConfiguration languageConfig;
    /**
     * The .yml locale, not parsed yet.
     */
    private File languageFile;
    /**
     * Players who haven't authenticated yet. Thread-safe.
     */
    private final Set<Player> playersAwaitingVerification = ConcurrentHashMap.newKeySet();
    /**
     * New players' passwords. Thread-safe.
     */
    private final Map<Integer, Player> newPlayers = new ConcurrentHashMap<>();
    /**
     * Major Bukkit release, for example 12 for Minecraft 1.12.2.
     */
    private final byte major = Byte.parseByte(Bukkit.getBukkitVersion().split("\\.")[1]);

    /**
     * You should NEVER call this method.
     */
    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    database.initialize();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this);

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        debug = getConfig().getBoolean("debug");

        createLanguageFiles("en", "it");
        languageFile = new File(getDataFolder(), "languages" + File.separator + getConfig().getString("language") + ".yml");
        if (!languageFile.exists()) {
            languageFile = new File(getDataFolder(), "languages" + File.separator + "en.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        registerListeners(
                //Login
                new LoginConfirmationListener(this),
                new PlayerJoinListener(this),
                new PlayerQuitListener(this),
                new PlayerRegisterListener(this),
                //Security - Block
                new BlockBreakListener(this),
                new BlockDamageListener(this),
                new BlockPlaceListener(this),
                new SignChangeListener(this),
                //Security - Enchantment
                new EnchantItemListener(this),
                new PrepareItemEnchantListener(this),
                //Security - Entity
                new EntityPickupItemListener(this),
                new EntityTameListener(this),
                new PlayerLeashEntityListener(this),
                new ProjectileLaunchListener(this),
                //Security - Hanging
                new HangingBreakByEntityListener(this),
                new HangingPlaceListener(this),
                //Security - Inventory
                new InventoryInteractListener(this),
                //Security - Player
                new AsyncPlayerChatListener(this),
                new PlayerBedEnterListener(this),
                new PlayerBucketEmptyListener(this),
                new PlayerBucketFillListener(this),
                new PlayerCommandPreprocessListener(this),
                new PlayerDropItemListener(this),
                new PlayerEditBookListener(this),
                new PlayerEggThrowListener(this),
                new PlayerFishListener(this),
                new PlayerInteractEntityListener(this),
                new PlayerInteractListener(this),
                new PlayerItemConsumeListener(this),
                new PlayerMoveListener(this),
                new PlayerShearEntityListener(this));

        if (major >= 12) {
            registerListeners(
                    //Security - Block
                    new BlockFertilizeListener(this));
        }

        if (major >= 9) {
            registerListeners(
                    //Security - Player
                    new PlayerItemMendListener(this)
            );
        }

        if (!getConfig().getString("bot.username").isEmpty() && !getConfig().getString("bot.token").isEmpty()) {
            bot = new Bot(this, getConfig().getString("bot.token"));
        }

        debug("onEnable() completed");
    }

    /**
     * You should NEVER call this method.
     */
    @Override
    public void onDisable() {
        bot.removeGetUpdatesListener();
    }

    /**
     * Shortcut for registering a {@link Listener} or more.
     *
     * @param listeners The listener(s) to register.
     */
    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
            debug(listener.getClass().getSimpleName() + " registered");
        }
    }

    /**
     * Shortcut for saving default locales, as instances of {@link FileConfiguration}.
     *
     * @param languages The locale(s) to save.
     */
    private void createLanguageFiles(String... languages) {
        for (String language : languages) {
            if (!new File(getDataFolder(), "languages" + File.separator + language + ".yml").exists()) {
                saveResource("languages" + File.separator + language + ".yml", false);
                debug(language + ".yml created");
            } else {
                debug(language + ".yml already exists");
            }
        }
    }

    /**
     * Sends debug messages to the server console if {@link #debug} is set to true.
     *
     * @param message The debug message to send.
     */
    public void debug(String message) {
        if (debug) {
            Bukkit.getConsoleSender().sendMessage("§3[TelegramLogin] §4[Debug]§7 " + message);
        }
    }

    /**
     * Replaces default placeholders, such as %prefix%, to the provided string.
     *
     * @param string The string where to replace placeholders.
     * @return The string with the placeholders replaced.
     */
    public String replacePlaceholders(String string) {
        return string.replace("%prefix%", languageConfig.getString("prefix"));
    }

    /**
     * Gets a formatted string from the chosen locale.
     *
     * @param path The path in the chosen locale from where to get the string.
     * @return The formatted string.
     * @see #getTranslatedStringList(String)
     */
    public String getTranslatedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(languageConfig.getString(path)));
    }

    /**
     * Gets a formatted {@link List}<{@link String}> from the chosen locale.
     *
     * @param path The path in the chosen locale from where to get the {@link List}<{@link String}>
     * @return The formatted {@link List}<{@link String}>
     * @see #getTranslatedString(String)
     */
    public List<String> getTranslatedStringList(String path) {
        return languageConfig.getStringList(path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', replacePlaceholders(s))).collect(Collectors.toList());
    }

    /**
     * Gets the Telegram bot instance, which sends and receives authentication messages.
     *
     * @return {@link #bot}
     */
    public Bot getBot() {
        return this.bot;
    }

    /**
     * Gets the SQLite database where users' UUIDs and Telegram IDs are stored.
     *
     * @return {@link #database}
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * Gets if TelegramLogin should send debugging messages to the server console.
     *
     * @return {@link #debug}
     */
    public boolean getDebug() {
        //Probably useless
        return this.debug;
    }

    /**
     * Gets the chosen locale, parsed.
     *
     * @return {@link #languageConfig}
     */
    public FileConfiguration getLanguageConfig() {
        //Probably useless
        return this.languageConfig;
    }

    /**
     * Gets the chosen locale, still not parsed.
     *
     * @return {@link #languageFile}
     */
    public File getLanguageFile() {
        //Probably useless
        return this.languageFile;
    }

    /**
     * Gets a thread-safe {@link Set} containing all players who haven't completed the verification yet.
     *
     * @return {@link #playersAwaitingVerification}
     */
    public Set<Player> getPlayersAwaitingVerification() {
        return this.playersAwaitingVerification;
    }

    /**
     * Gets a thread-safe {@link Map} containing new players' passwords as keys and new players as values.
     *
     * @return {@link #newPlayers}
     */
    public Map<Integer, Player> getNewPlayers() {
        return this.newPlayers;
    }

    /**
     * Gets the major Bukkit release, for example 12 for Minecraft 1.12.2.
     *
     * @return {@link #major}
     */
    public byte getMajor() {
        //Probably useless
        return this.major;
    }

    /**
     * Sets if TelegramLogin should send debugging messages to the server console.
     *
     * @param debug <code>true</code> if such messages should be sent, <code>false</code> otherwise
     */
    public void setDebug(boolean debug) {
        //Will be used with /telegramlogin reload
        this.debug = debug;
    }
}
