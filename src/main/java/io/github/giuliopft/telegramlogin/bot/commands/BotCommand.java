package io.github.giuliopft.telegramlogin.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;

public abstract class BotCommand {
    protected final TelegramLogin telegramLogin;
    protected final long chatId;
    protected final String[] arguments;

    protected BotCommand(TelegramLogin telegramLogin, long chatId, String[] arguments) {
        this.telegramLogin = telegramLogin;
        this.chatId = chatId;
        this.arguments = arguments;
    }

    public abstract SendMessage run();
}
