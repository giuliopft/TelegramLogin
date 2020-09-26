package io.github.giuliopft.telegramlogin.bot.commands;

import io.github.giuliopft.telegramlogin.TelegramLogin;

import java.util.Arrays;

/**
 * This class generates {@link BotCommand} instances.
 */
public class BotCommandFactory {
    /**
     * Main class instance.
     */
    private final TelegramLogin telegramLogin;

    /**
     * Creates a new BotCommandFactory.
     *
     * @param telegramLogin Main class instance.
     */
    public BotCommandFactory(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    /**
     * Creates and returns an instance of a subclass of {@link BotCommand}.
     *
     * @param chatId The chat where the message was sent.
     * @param text   The message, split where space characters occur.
     * @return A {@link BotCommand} instance, or <code>null</code> if the message didn't contain any valid commands.
     */
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
