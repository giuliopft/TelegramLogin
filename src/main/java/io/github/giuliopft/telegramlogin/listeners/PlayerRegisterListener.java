package io.github.giuliopft.telegramlogin.listeners;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.events.PlayerRegisterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class PlayerRegisterListener implements Listener {
    private final TelegramLogin telegramLogin;

    public PlayerRegisterListener(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    @EventHandler
    public void onPlayerRegister(PlayerRegisterEvent event) {
        if (!telegramLogin.getNewPlayers().containsKey(event.getPassword())) {
            event.setState(PlayerRegisterEvent.State.WRONG_PASSWORD);
            telegramLogin.debug(event.getPassword() + " is not a valid password");
            return;
        }

        boolean isUnique = true;
        try {
            isUnique = telegramLogin.getDatabase().get(event.getTelegramId()).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean allowMultipleAccounts = telegramLogin.getConfig().getBoolean("login.allow-multiple-accounts");
        boolean isAllowMultipleAccountsException = telegramLogin.getConfig().getIntegerList("login.allow-multiple-accounts-exceptions").contains(event.getTelegramId());

        Player player = telegramLogin.getNewPlayers().remove(event.getPassword());
        telegramLogin.getPlayersAwaitingVerification().remove(player);

        if (!isUnique && !allowMultipleAccounts && !isAllowMultipleAccountsException) {
            event.setState(PlayerRegisterEvent.State.MULTIPLE_ACCOUNTS);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.kickPlayer(telegramLogin.getTranslatedString("minecraft.kick-message-multiple-accounts"));
                }
            }.runTask(telegramLogin);
            telegramLogin.debug(event.getTelegramId() + " already has an account");
        } else {
            player.sendMessage(telegramLogin.getTranslatedString("minecraft.successful"));
            event.setPlayer(player);
            try {
                telegramLogin.getDatabase().add(player.getUniqueId(), event.getTelegramId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            event.setState(PlayerRegisterEvent.State.SUCCESSFUL);
        }
    }
}
