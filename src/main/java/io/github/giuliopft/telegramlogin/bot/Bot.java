package io.github.giuliopft.telegramlogin.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.events.PlayerRegisterEvent;
import org.bukkit.Bukkit;

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
                    telegramLogin.debug(update.message().from().id() + " has just sent this message to the bot: " + update.message().text());
                    switch (update.message().text().toLowerCase().split(" ")[0]) {
                        case ".start":
                        case "/start":
                            onStart(update.message().chat().id());
                            break;
                        case ".r":
                        case "/r":
                            onR(update.message().chat().id(), update.message().text());
                            break;
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
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
}
