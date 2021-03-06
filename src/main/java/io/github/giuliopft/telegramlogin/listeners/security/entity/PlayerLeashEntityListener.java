package io.github.giuliopft.telegramlogin.listeners.security.entity;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public class PlayerLeashEntityListener implements Listener {
    private final TelegramLogin telegramLogin;

    public PlayerLeashEntityListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        if (telegramLogin.getPlayersAwaitingVerification().contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
        }
    }
}
