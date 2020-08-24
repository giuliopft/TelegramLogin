package io.github.giuliopft.telegramlogin.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoginConfirmationEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final int telegramId;
    @Getter
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

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
