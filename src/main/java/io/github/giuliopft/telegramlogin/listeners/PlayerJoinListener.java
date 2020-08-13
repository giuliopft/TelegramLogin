package io.github.giuliopft.telegramlogin.listeners;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TelegramLogin telegramLogin;

    public PlayerJoinListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getName().equals("giuliopft")) {
            event.getPlayer().sendMessage("&3[TelegramLogin]&a This server is using TelegramLogin " + telegramLogin.getDescription().getVersion() + "!");
        }
    }
}
