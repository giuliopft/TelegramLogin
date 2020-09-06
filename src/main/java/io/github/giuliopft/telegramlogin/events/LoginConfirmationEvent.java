package io.github.giuliopft.telegramlogin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoginConfirmationEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int telegramId;
    private final boolean confirmed;

    public LoginConfirmationEvent(int telegramId, boolean confirmed) {
        super(true);
        this.telegramId = telegramId;
        this.confirmed = confirmed;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public int getTelegramId() {
        return this.telegramId;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }
}
