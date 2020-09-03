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
import io.github.giuliopft.telegramlogin.events.LoginConfirmationEvent;
import io.github.giuliopft.telegramlogin.events.PlayerRegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Bot extends TelegramBot {
    private final TelegramLogin telegramLogin;

    public Bot(TelegramLogin telegramLogin, String token) {
        super(token);
        this.telegramLogin = telegramLogin;
        setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null) {
                    if (update.message().chat().type() != Chat.Type.Private) {
                        continue;
                    }
                    onMessage(update.message());
                } else if (update.callbackQuery() != null) {
                    onCallbackQuery(update.callbackQuery());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void onMessage(Message message) {
        telegramLogin.debug(message.from().id() + " has just sent this message to the bot: " + message.text());
        switch (message.text().toLowerCase().split(" ")[0]) {
            case ".start":
            case "/start":
                onStart(message.chat().id());
                break;
            case ".r":
            case "/r":
                onR(message.chat().id(), message.text());
                break;
            default:
                error(message.chat().id());
                break;
        }
    }

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

    private void onStart(long chatId) {
        execute(new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.start"))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true));
    }

    private void onR(long chatId, String text) {
        if (text.split(" ")[1].matches("\\d+")) {
            PlayerRegisterEvent event = new PlayerRegisterEvent(new Integer(text.split(" ")[1]), (int) chatId);
            Bukkit.getPluginManager().callEvent(event);
            if (event.getState() == PlayerRegisterEvent.State.WRONG_PASSWORD) {
                error(chatId);
            } else {
                SendMessage sendMessage = null;
                switch (event.getState()) {
                    case SUCCESSFUL:
                        sendMessage = new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.successful-registration")
                                .replace("%player%", event.getPlayer().getName())).parseMode(ParseMode.HTML).disableWebPagePreview(true);
                        break;
                    case MULTIPLE_ACCOUNTS:
                        sendMessage = new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.multiple-accounts"))
                                .parseMode(ParseMode.HTML).disableWebPagePreview(true);
                        break;
                }
                execute(sendMessage);
            }
        } else {
            error(chatId);
        }
    }

    private void error(long chatId) {
        execute(new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.error"))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true));
    }

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
