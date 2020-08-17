package io.github.giuliopft.telegramlogin.listeners;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;

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

        boolean forceLogin = telegramLogin.getConfig().getBoolean("login.force-login");
        boolean isForceLoginException = telegramLogin.getConfig().getStringList("login.force-login-exceptions").contains(
                telegramLogin.getConfig().getBoolean("login.use-uuids") ? event.getPlayer().getUniqueId() : event.getPlayer().getName());
        telegramLogin.debug(event.getPlayer().getName() + "is " + (isForceLoginException ? "" : "not") + " an exception");
        boolean isNew = true;
        try {
            isNew = telegramLogin.getDatabase().get(event.getPlayer().getUniqueId()).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        telegramLogin.debug(event.getPlayer().getName() + "is " + (isNew ? "" : "not") + " new");
        boolean forceNew = telegramLogin.getConfig().getBoolean("login.force-new-players-to-login");

        if (!forceLogin && !isForceLoginException && (!isNew || !forceNew)) {
            return;
        }

        telegramLogin.getPlayersAwaitingVerification().add(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (telegramLogin.getPlayersAwaitingVerification().contains(event.getPlayer())) {
                    event.getPlayer().kickPlayer(telegramLogin.getTranslatedString("kick-message"));
                }
            }
        }.runTaskLater(telegramLogin, telegramLogin.getConfig().getInt("login.idle-time") * 20);

        if (isNew) {
            //Tell the player to start the bot
        } else {
            int id = 0;
            try {
                ResultSet resultSet = telegramLogin.getDatabase().get(event.getPlayer().getUniqueId());
                resultSet.next();
                id = resultSet.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Send login message to id
        }
    }
}
