package io.github.giuliopft.telegramlogin;

import org.bukkit.ChatColor;

public class Utils {

    private final TelegramLogin telegramLogin;

    public Utils(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    public String applyPlaceholders(String string) {
        return string.replace("{prefix}", telegramLogin.getLanguageConfig().getString("prefix"));
    }

    public String getTranslatedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholders(telegramLogin.getLanguageConfig().getString(path)));
    }
}
