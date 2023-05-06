package com.georgyorlov.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TorrentsDownloadBot extends TelegramLongPollingCommandBot {

    private static Logger LOGGER = LoggerFactory.getLogger(TorrentsDownloadBot.class);

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            LOGGER.info("User [{}] send message [{}]",
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getText()
            );
        }
    }

    @Override
    public String getBotUsername() {
        return System.getProperty("botUsername");
    }

    @Override
    public String getBotToken() {
        return System.getProperty("botToken");
    }
}
