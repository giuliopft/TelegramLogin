package io.github.giuliopft.telegramlogin.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRegisterEvent extends Event {
    public enum State {
        SUCCESSFUL,
        MULTIPLE_ACCOUNTS,
        WRONG_PASSWORD
    }

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    @Setter
    private volatile State state;
    @Getter
    private final int password;
    @Getter
    private final int telegramId;
    @Getter
    @Setter
    private volatile Player player;

    public PlayerRegisterEvent(int password, int telegramId) {
        super(true);
        this.password = password;
        this.telegramId = telegramId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
