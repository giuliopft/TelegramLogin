package io.github.giuliopft.telegramlogin.listeners.login;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final TelegramLogin telegramLogin;

    public PlayerQuitListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        telegramLogin.getPlayersAwaitingVerification().remove(event.getPlayer());
        telegramLogin.getNewPlayers().values().remove(event.getPlayer());
    }
}
