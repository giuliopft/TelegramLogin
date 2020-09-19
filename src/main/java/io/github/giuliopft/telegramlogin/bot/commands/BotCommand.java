package io.github.giuliopft.telegramlogin.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;

/**
 * This abstract class represents any commands the bot should handle.
 */
public abstract class BotCommand {
    /**
     * Main class instance.
     */
    protected final TelegramLogin telegramLogin;
    /**
     * The chat where the command comes from.
     */
    protected final long chatId;
    /**
     * Any arguments following the command, for instance {"hello", "world"} if the bot receives "/say hello world".
     */
    protected final String[] arguments;

    /**
     * Creates a new BotCommand instance.
     *
     * @param telegramLogin Main class instance.
     * @param chatId        The chat where the command comes from.
     * @param arguments     Any arguments following the command.
     */
    protected BotCommand(TelegramLogin telegramLogin, long chatId, String[] arguments) {
        this.telegramLogin = telegramLogin;
        this.chatId = chatId;
        this.arguments = arguments;
    }

    /**
     * Handles the command received.
     *
     * @return A reply which is ready to be sent.
     */
    public abstract SendMessage run();
}
