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
import io.github.giuliopft.telegramlogin.listeners.security.player.PlayerBucketListener;
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
import lombok.Getter;
import lombok.Setter;
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

public final class TelegramLogin extends JavaPlugin {
    @Getter
    private Bot bot;
    @Getter
    private final Database database = new Database(this);
    @Getter
    @Setter
    private boolean debug;
    @Getter
    private FileConfiguration languageConfig;
    @Getter
    private File languageFile;
    @Getter
    private final Set<Player> playersAwaitingVerification = ConcurrentHashMap.newKeySet();
    @Getter
    private final Map<Integer, Player> newPlayers = new ConcurrentHashMap<>();
    @Getter
    private final byte major = Byte.parseByte(Bukkit.getBukkitVersion().split("\\.")[1]);

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
                new PlayerBucketListener(this),
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

    @Override
    public void onDisable() {
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
            debug(listener.getClass().getSimpleName() + " registered");
        }
    }

    private void createLanguageFiles(String... languages) {
        for (String language : languages) {
            saveResource("languages" + File.separator + language + ".yml", false);
            debug(language + ".yml created");
        }
    }

    public void debug(String message) {
        if (debug) {
            Bukkit.getConsoleSender().sendMessage("§3[TelegramLogin] §4[Debug]§7 " + message);
        }
    }

    public String applyPlaceholders(String string) {
        return string.replace("%prefix%", getLanguageConfig().getString("prefix"));
    }

    public String getTranslatedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholders(getLanguageConfig().getString(path)));
    }

    public List<String> getTranslatedStringList(String path) {
        return getLanguageConfig().getStringList(path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', applyPlaceholders(s))).collect(Collectors.toList());
    }
}
