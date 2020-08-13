package io.github.giuliopft.telegramlogin;

import org.bukkit.ChatColor;

public class Utils {
    public static String applyPlaceholders(String string) {
        return string.replace("{prefix}", TelegramLogin.getInstance().getLanguageConfig().getString("prefix"));
    }

    public static String getTranslatedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholders(TelegramLogin.getInstance().getLanguageConfig().getString(path)));
    }
}
