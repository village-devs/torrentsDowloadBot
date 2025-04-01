package com.georgyorlov.torrentdownloader.bot;

import com.georgyorlov.torrentdownloader.command.DownloadCommand;
import com.georgyorlov.torrentdownloader.command.StartCommand;
import com.georgyorlov.torrentdownloader.command.StopCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;

@Slf4j
@Component
public class TorrentsDownloadBot extends CommandLongPollingTelegramBot {


    public TorrentsDownloadBot(DownloadCommand downloadCommand,
                               StartCommand startCommand,
                               StopCommand stopCommand) {
        super(new OkHttpTelegramClient(System.getenv("botToken")), true, () -> System.getenv("botUsername"));
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
            log.info("User [{}] chatId [{}] send message [{}] at [{}]",
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getChat().getId(),
                    update.getMessage().getText(),
                    Instant.ofEpochSecond(update.getMessage().getDate()) //long()*1000
            );
            SendMessage message = new SendMessage(update.getMessage().getChatId().toString(), "Bot is under construction. Sorry for the inconvenience");

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                //todo
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void test() throws TelegramApiException {
        telegramClient.execute(SendMessage.builder().chatId(66580861L).text("kek").build());
    }

}
