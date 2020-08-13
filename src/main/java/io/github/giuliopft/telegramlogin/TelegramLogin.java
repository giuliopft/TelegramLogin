package io.github.giuliopft.telegramlogin;

import io.github.giuliopft.telegramlogin.listeners.PlayerJoinListener;
import io.github.giuliopft.telegramlogin.sql.Database;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TelegramLogin extends JavaPlugin {

    @Getter
    private final Database database = new Database(this);

    @Getter
    private final Utils utils = new Utils(this);

    @Getter
    private File languageFile;
    @Getter
    private FileConfiguration languageConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        createLanguageFiles("en", "it");
        languageFile = new File(getDataFolder(), "languages" + File.separator + getConfig().getString("language") + ".yml");
        if (!languageFile.exists()) {
            languageFile = new File(getDataFolder(), "languages" + File.separator + "en.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        registerListeners(new PlayerJoinListener(this));
    }

    @Override
    public void onDisable() {
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void createLanguageFiles(String... languages) {
        for (String language : languages) {
            saveResource("languages" + File.separator + language + ".yml", false);
        }
    }

    public void debug(String message) {
        if (getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§3[TelegramLogin] §4[Debug]§7 " + message);
        }
    }
}
