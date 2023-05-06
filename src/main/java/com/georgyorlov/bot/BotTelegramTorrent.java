package com.georgyorlov.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotTelegramTorrent {

    private static Logger LOGGER = LoggerFactory.getLogger(BotTelegramTorrent.class);

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TorrentsDownloadBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
