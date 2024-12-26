package com.georgyorlov.torrentdownloader.bot;

import com.georgyorlov.torrentdownloader.command.DownloadCommand;
import com.georgyorlov.torrentdownloader.command.StartCommand;
import com.georgyorlov.torrentdownloader.command.StopCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;

@Component
public class TorrentsDownloadBot extends TelegramLongPollingCommandBot {

    private static Logger LOGGER = LoggerFactory.getLogger(TorrentsDownloadBot.class);

    public TorrentsDownloadBot(DownloadCommand downloadCommand,
                               StartCommand startCommand,
                               StopCommand stopCommand) {
        super();
        registerAll(downloadCommand, startCommand, stopCommand);
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
            LOGGER.info("User [{}] chatId [{}] send message [{}] at [{}]",
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getChat().getId(),
                    update.getMessage().getText(),
                    Instant.ofEpochSecond(update.getMessage().getDate()) //long()*1000
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
        return System.getenv("botUsername");
    }

    @Override
    public String getBotToken() {
        return System.getenv("botToken");
    }
}
