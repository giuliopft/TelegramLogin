package io.github.giuliopft.telegramlogin.listeners.security.entity;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTameListener implements Listener {
    private final TelegramLogin telegramLogin;

    public EntityTameListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTame(EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            Player player = (Player) event.getOwner();
            if (telegramLogin.getPlayersAwaitingVerification().contains(player)) {
                event.setCancelled(true);
                player.sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
            }
        }
    }
}
