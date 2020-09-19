package io.github.giuliopft.telegramlogin.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.bot.commands.BotCommand;
import io.github.giuliopft.telegramlogin.bot.commands.BotCommandFactory;
import io.github.giuliopft.telegramlogin.events.LoginConfirmationEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class represents the bot that players interact with in order to login.
 */
public class Bot extends TelegramBot {
    /**
     * Main class instance.
     */
    private final TelegramLogin telegramLogin;
    /**
     * Used to handle commands.
     */
    private final BotCommandFactory botCommandFactory;

    /**
     * Creates a bot instance and prepares an update listener to receive and handle Telegram messages and callback queries.
     * You should not need to call this method.
     *
     * @param telegramLogin Main class instance.
     * @param token         Token of your bot on Telegram, obtainable from BotFather.
     */
    public Bot(TelegramLogin telegramLogin, String token) {
        super(token);
        this.telegramLogin = telegramLogin;
        botCommandFactory = new BotCommandFactory(telegramLogin);
        setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().chat().type() == Chat.Type.Private) {
                    onMessage(update.message());
                } else if (update.callbackQuery() != null) {
                    onCallbackQuery(update.callbackQuery());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * Handles messages and sends a reply, using {@link #botCommandFactory} to select the right answer.
     *
     * @param message The message received.
     */
    private void onMessage(Message message) {
        telegramLogin.debug(message.from().id() + " has just sent this message to the bot: " + message.text());
        SendMessage answer;
        if (message.text() == null) {
            answer = error(message.chat().id());
        } else {
            BotCommand command = botCommandFactory.getCommand(message.chat().id(), message.text().split(" "));
            if (command == null) {
                answer = error(message.chat().id());
            } else {
                answer = command.run();
            }
        }
        execute(answer);
    }

    /**
     * Handles callback queries, kicking or allowing a player to join the server.
     *
     * @param callbackQuery The callback query received.
     */
    private void onCallbackQuery(CallbackQuery callbackQuery) {
        telegramLogin.debug(callbackQuery.from().id() + " has just sent this callback query to the bot: " + callbackQuery.data());
        LoginConfirmationEvent event = new LoginConfirmationEvent(callbackQuery.from().id(), callbackQuery.data().equals("yes"));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isConfirmed()) {
            execute(new EditMessageText(callbackQuery.message().chat().id(), callbackQuery.message().messageId(),
                    telegramLogin.getTranslatedString("telegram.successful-authentication")));
        } else {
            execute(new EditMessageText(callbackQuery.message().chat().id(), callbackQuery.message().messageId(),
                    telegramLogin.getTranslatedString("telegram.unsuccessful-authentication")));
        }
        execute(new EditMessageReplyMarkup(callbackQuery.message().chat().id(), callbackQuery.message().messageId()).replyMarkup(new InlineKeyboardMarkup()));
        execute(new AnswerCallbackQuery(callbackQuery.id()));
    }

    /**
     * Gets a default error message, but doesn't send it.
     *
     * @param chatId The chat where to send an error message.
     * @return A default error message.
     */
    public SendMessage error(long chatId) {
        return new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.error"))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);
    }

    /**
     * Sends a login confirmation message when someone tries to join the server.
     *
     * @param chatId The chat where to send such message.
     * @param player The player who is trying to join the server.
     * @return The message ID, once it has been sent.
     */
    public int login(long chatId, Player player) {
        return execute(new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.login.message").replace("%player%", player.getName()))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton(telegramLogin.getTranslatedString("telegram.login.button-yes")).callbackData("yes"),
                        new InlineKeyboardButton(telegramLogin.getTranslatedString("telegram.login.button-no")).callbackData("no")})))
                .message().messageId();
    }
}
