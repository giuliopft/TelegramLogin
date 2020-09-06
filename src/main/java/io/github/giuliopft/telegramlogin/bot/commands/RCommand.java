package io.github.giuliopft.telegramlogin.bot.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.giuliopft.telegramlogin.TelegramLogin;
import io.github.giuliopft.telegramlogin.events.PlayerRegisterEvent;
import org.bukkit.Bukkit;

public class RCommand extends BotCommand {
    public RCommand(TelegramLogin telegramLogin, long chatId, String[] arguments) {
        super(telegramLogin, chatId, arguments);
    }

    @Override
    public SendMessage run() {
        if (arguments.length > 1 || !arguments[0].matches("\\d+")) {
            return telegramLogin.getBot().error(chatId);
        }

        PlayerRegisterEvent event = new PlayerRegisterEvent(new Integer(arguments[0]), chatId);
        Bukkit.getPluginManager().callEvent(event);

        if (event.getState() == PlayerRegisterEvent.State.WRONG_PASSWORD) {
            return telegramLogin.getBot().error(chatId);
        }

        switch (event.getState()) {
            case SUCCESSFUL:
                return new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.successful-registration")
                        .replace("%player%", event.getPlayer().getName())).parseMode(ParseMode.HTML).disableWebPagePreview(true);
            case MULTIPLE_ACCOUNTS:
                return new SendMessage(chatId, telegramLogin.getTranslatedString("telegram.multiple-accounts"))
                        .parseMode(ParseMode.HTML).disableWebPagePreview(true);
        }
        return null;
    }
}
