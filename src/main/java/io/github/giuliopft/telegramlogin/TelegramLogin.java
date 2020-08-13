package io.github.giuliopft.telegramlogin;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TelegramLogin extends JavaPlugin {

    @Getter
    private static TelegramLogin instance;

    @Getter
    private File languageFile;
    @Getter
    private FileConfiguration languageConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        createLanguageFile("en", "it");
        languageFile = new File(getDataFolder(), "languages" + File.separator + getConfig().getString("language") + ".yml");
        if (!languageFile.exists()) {
            languageFile = new File(getDataFolder(), "languages" + File.separator + "en.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    @Override
    public void onDisable() {
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void createLanguageFile(String... languages) {
        for (String language : languages) {
            saveResource("languages" + File.separator + language + ".yml", false);
        }
    }
}
