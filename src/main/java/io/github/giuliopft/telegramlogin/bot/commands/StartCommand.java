package io.github.giuliopft.telegramlogin.bot.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;

public class StartCommand extends BotCommand {
    public StartCommand(TelegramLogin telegramLogin, long chatId, String[] arguments) {
        super(telegramLogin, chatId, arguments);
    }

    @Override
    public SendMessage run() {
        return new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.start"))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);
    }
}
