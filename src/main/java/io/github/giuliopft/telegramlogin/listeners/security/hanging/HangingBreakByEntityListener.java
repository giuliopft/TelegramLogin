package io.github.giuliopft.telegramlogin.listeners.security.hanging;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreakByEntityListener implements Listener {
    private final TelegramLogin telegramLogin;

    public HangingBreakByEntityListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();
            if (telegramLogin.getPlayersAwaitingVerification().contains(player)) {
                event.setCancelled(true);
                player.sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
            }
        }
    }
}
