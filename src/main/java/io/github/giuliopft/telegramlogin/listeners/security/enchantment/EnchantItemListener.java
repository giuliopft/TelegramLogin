package io.github.giuliopft.telegramlogin.listeners.security.enchantment;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItemListener implements Listener {
    private final TelegramLogin telegramLogin;

    public EnchantItemListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {
        if (telegramLogin.getPlayersAwaitingVerification().contains(event.getEnchanter())) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
        }
    }
}
