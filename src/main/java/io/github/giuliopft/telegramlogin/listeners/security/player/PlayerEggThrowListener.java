package io.github.giuliopft.telegramlogin.listeners.security.player;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class PlayerEggThrowListener implements Listener {
    private final TelegramLogin telegramLogin;

    public PlayerEggThrowListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        if (telegramLogin.getPlayersAwaitingVerification().contains(event.getPlayer())) {
            event.setHatching(false);
            event.getPlayer().sendMessage(telegramLogin.getTranslatedString("minecraft.you-cannot-do-this"));
        }
    }
}
