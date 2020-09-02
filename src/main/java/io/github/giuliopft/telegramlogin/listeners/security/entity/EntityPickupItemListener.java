package io.github.giuliopft.telegramlogin.listeners.security.entity;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {
    private final TelegramLogin telegramLogin;

    public EntityPickupItemListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (telegramLogin.getPlayersAwaitingVerification().contains(player)) {
                event.setCancelled(true);
                player.sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
            }
        }
    }
}
