package io.github.giuliopft.telegramlogin.listeners;

import com.pengrad.telegrambot.request.DeleteMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

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
        boolean forceNew = telegramLogin.getConfig().getBoolean("login.force-new-players-to-login");
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean isNew = true;
                try {
                    isNew = telegramLogin.getDatabase().get(event.getPlayer().getUniqueId()).next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                telegramLogin.debug(event.getPlayer().getName() + "is " + (isNew ? "" : "not") + " new");
                if (forceLogin || isForceLoginException || (isNew && forceNew)) {
                    telegramLogin.getPlayersAwaitingVerification().add(event.getPlayer());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (telegramLogin.getPlayersAwaitingVerification().contains(event.getPlayer())) {
                                event.getPlayer().kickPlayer(telegramLogin.getTranslatedString("minecraft.kick-message-too-much-time"));
                            }
                        }
                    }.runTaskLater(telegramLogin, telegramLogin.getConfig().getInt("login.idle-time") * 20);

                    if (isNew) {
                        int password = new Random().nextInt(Integer.MAX_VALUE);
                        telegramLogin.getNewPlayers().put(password, event.getPlayer());
                        telegramLogin.debug(event.getPlayer().getName() + "'s password is " + password);
                        event.getPlayer().sendMessage(telegramLogin.getTranslatedString("minecraft.new-user")
                                .replace("%player%", event.getPlayer().getName())
                                .replace("%bot-username%", telegramLogin.getConfig().getString("username"))
                                .replace("%command%", "/r " + password));
                    } else {
                        int id = 0;
                        try {
                            ResultSet resultSet = telegramLogin.getDatabase().get(event.getPlayer().getUniqueId());
                            resultSet.next();
                            id = resultSet.getInt("id");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        int message = telegramLogin.getBot().login(id, event.getPlayer());
                        int finalId = id;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                telegramLogin.getBot().execute(new DeleteMessage(finalId, message));
                            }
                        }.runTaskLaterAsynchronously(telegramLogin, 20 * telegramLogin.getConfig().getInt("login.idle-time"));
                        event.getPlayer().sendMessage(telegramLogin.getTranslatedString("minecraft.login"));
                    }
                }
            }
        }.runTaskAsynchronously(telegramLogin);
    }
}
