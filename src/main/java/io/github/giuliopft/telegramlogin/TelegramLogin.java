package io.github.giuliopft.telegramlogin;

import io.github.giuliopft.telegramlogin.bot.Bot;
import io.github.giuliopft.telegramlogin.listeners.PlayerJoinListener;
import io.github.giuliopft.telegramlogin.listeners.PlayerQuitListener;
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
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<Player> playersAwaitingVerification = new HashSet<>();

    @Override
    public void onEnable() {
        try {
            database.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        debug = getConfig().getBoolean("debug");

        createLanguageFiles("en", "it");
        languageFile = new File(getDataFolder(), "languages" + File.separator + getConfig().getString("language") + ".yml");
        if (!languageFile.exists()) {
            languageFile = new File(getDataFolder(), "languages" + File.separator + "en.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        registerListeners(new PlayerJoinListener(this), new PlayerQuitListener(this));

        ApiContextInitializer.init();
        bot = new Bot(getConfig().getString("bot.username"), getConfig().getString("bot.token"));
        try {
            new TelegramBotsApi().registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
            debug(listener.getClass().getSimpleName() + "registered");
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
        return string.replace("{prefix}", getLanguageConfig().getString("prefix"));
    }

    public String getTranslatedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholders(getLanguageConfig().getString(path)));
    }
}
