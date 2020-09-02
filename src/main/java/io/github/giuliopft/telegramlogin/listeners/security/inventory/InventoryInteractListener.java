package io.github.giuliopft.telegramlogin.listeners.security.inventory;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class InventoryInteractListener implements Listener {
    private final TelegramLogin telegramLogin;

    public InventoryInteractListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (telegramLogin.getPlayersAwaitingVerification().contains(player)) {
            event.setCancelled(true);
            player.sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
        }
    }
}
