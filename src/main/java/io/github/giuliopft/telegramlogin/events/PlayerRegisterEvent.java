package io.github.giuliopft.telegramlogin.events;

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
    private volatile State state;
    private final int password;
    private final long telegramId;
    private volatile Player player;

    public PlayerRegisterEvent(int password, long telegramId) {
        super(true);
        this.password = password;
        this.telegramId = telegramId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public State getState() {
        return this.state;
    }

    public int getPassword() {
        return this.password;
    }

    public long getTelegramId() {
        return this.telegramId;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
