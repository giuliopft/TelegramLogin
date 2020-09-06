package io.github.giuliopft.telegramlogin.bot.commands;

import io.github.giuliopft.telegramlogin.TelegramLogin;

import java.util.Arrays;

public class BotCommandFactory {
    private final TelegramLogin telegramLogin;

    public BotCommandFactory(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    public BotCommand getCommand(long chatId, String[] text) {
        switch (text[0]) {
            case ".start":
            case "/start":
                return new StartCommand(telegramLogin, chatId, Arrays.copyOfRange(text, 1, text.length));
            case ".r":
            case "/r":
                return new RCommand(telegramLogin, chatId, Arrays.copyOfRange(text, 1, text.length));
            default:
                return null;
        }
    }
}
