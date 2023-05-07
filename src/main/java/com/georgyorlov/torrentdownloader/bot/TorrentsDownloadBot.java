package com.georgyorlov.torrentdownloader.bot;

import com.georgyorlov.torrentdownloader.command.DownloadCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TorrentsDownloadBot extends TelegramLongPollingCommandBot {

    private static Logger LOGGER = LoggerFactory.getLogger(TorrentsDownloadBot.class);

    public TorrentsDownloadBot() {
        super();
        register(new DownloadCommand());
/*        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help ");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                //puk
            }
            //helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
        });*/
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            LOGGER.info("User [{}] send message [{}]",
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getText()
            );
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText("Bot is under construction. Sorry for the inconvenience");

            try {
                execute(message);
            } catch (TelegramApiException e) {
                //todo
            }
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
