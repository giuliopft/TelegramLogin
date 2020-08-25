package io.github.giuliopft.telegramlogin.listeners;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.events.LoginConfirmationEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginConfirmationListener implements Listener {
    private final TelegramLogin telegramLogin;

    public LoginConfirmationListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler
    public void onLoginConfirmation(LoginConfirmationEvent event) {
        try {
            ResultSet accounts = telegramLogin.getDatabase().get(event.getTelegramId());
            while (accounts.next()) {
                UUID uuid = UUID.fromString(accounts.getString("uuid"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(uuid);
                        telegramLogin.debug("Checking player " + player.getName());
                        if (!telegramLogin.getPlayersAwaitingVerification().contains(player)) {
                            telegramLogin.debug("Player " + player.getName() + " is not awaiting verification");
                            return;
                        }
                        if (event.isConfirmed()) {
                            telegramLogin.debug("Player " + player.getName() + " accepted");
                            telegramLogin.getPlayersAwaitingVerification().remove(player);
                            player.sendMessage(telegramLogin.getTranslatedString("minecraft.successful"));
                        } else {
                            telegramLogin.debug("Player " + player.getName() + " kicked");
                            player.kickPlayer(telegramLogin.getTranslatedString("minecraft.kick-message-it-is-not-you"));
                        }
                    }
                }.runTask(telegramLogin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}