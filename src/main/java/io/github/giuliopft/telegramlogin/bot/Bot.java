package io.github.giuliopft.telegramlogin.bot;

import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.events.PlayerRegisterEvent;
import org.bukkit.Bukkit;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private final TelegramLogin telegramLogin;
    private final String username;
    private final String token;

    public Bot(TelegramLogin telegramLogin, String username, String token) {
        this.telegramLogin = telegramLogin;
        this.username = username;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (!update.getMessage().isUserMessage()) {
                return;
            }
            telegramLogin.debug(update.getMessage().getFrom().getId() + " has just sent this message to the bot: " + update.getMessage().getText());
            switch (update.getMessage().getText().toLowerCase().split(" ")[0]) {
                case ".start":
                case "/start":
                    onStart(update.getMessage().getChatId());
                    break;
                case ".r":
                case "/r":
                    onR(update.getMessage().getChatId(), update.getMessage().getText());
                    break;
            }
        }
    }

    private void onStart(long chatId) {
        try {
            execute(new SendMessage()
                    .setChatId(chatId)
                    .enableHtml(true)
                    .disableWebPagePreview()
                    .setText(telegramLogin.getTranslatedString("telegram.start")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void onR(long chatId, String text) {
        if (text.split(" ")[1].matches("\\d+")) {
            PlayerRegisterEvent event = new PlayerRegisterEvent(new Integer(text.split(" ")[1]), (int) chatId);
            Bukkit.getPluginManager().callEvent(event);
            if (event.getState() == PlayerRegisterEvent.State.WRONG_PASSWORD) {
                error(chatId);
            } else {
                SendMessage sendMessage = new SendMessage()
                        .setChatId(chatId)
                        .enableHtml(true)
                        .disableWebPagePreview();
                switch (event.getState()) {
                    case SUCCESSFUL:
                        sendMessage.setText(telegramLogin.getTranslatedString("telegram.successful-registration").replace("%player%", event.getPlayer().getName()));
                        break;
                    case MULTIPLE_ACCOUNTS:
                        sendMessage.setText(telegramLogin.getTranslatedString("telegram.multiple-accounts"));
                        break;
                }
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            error(chatId);
        }
    }

    private void error(long chatId) {
        try {
            execute(new SendMessage()
                    .setChatId(chatId)
                    .enableHtml(true)
                    .disableWebPagePreview()
                    .setText(telegramLogin.getTranslatedString("telegram.error")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
