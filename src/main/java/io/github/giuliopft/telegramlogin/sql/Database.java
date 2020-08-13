package io.github.giuliopft.telegramlogin.sql;

import io.github.giuliopft.telegramlogin.TelegramLogin;

import java.io.File;

public final class Database {

    private final TelegramLogin telegramLogin;
    private final String path;

    public Database(TelegramLogin telegramLogin) {
        this.telegramLogin = telegramLogin;
        this.path = "jdbc:sqlite:" + telegramLogin.getDataFolder() + File.separator + "data.db";
    }
}
