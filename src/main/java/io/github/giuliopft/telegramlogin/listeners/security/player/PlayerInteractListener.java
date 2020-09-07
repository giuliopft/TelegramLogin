package io.github.giuliopft.telegramlogin.listeners.security.player;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final TelegramLogin telegramLogin;

    public PlayerInteractListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (telegramLogin.getPlayersAwaitingVerification().contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
        }
    }
}
