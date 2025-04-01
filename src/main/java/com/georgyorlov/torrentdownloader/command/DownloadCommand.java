package com.georgyorlov.torrentdownloader.command;

import com.georgyorlov.torrentdownloader.download.Download;
import com.georgyorlov.torrentdownloader.download.DownloadDaoService;
import com.georgyorlov.torrentdownloader.download.DownloadStatus;
import com.georgyorlov.torrentdownloader.user.TelegramUser;
import com.georgyorlov.torrentdownloader.user.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class DownloadCommand extends BotCommand {

    private final TelegramUserService telegramUserService;
    private final DownloadDaoService downloadDaoService;
    //private final TelegramClient telegramClient;

    public DownloadCommand(TelegramUserService telegramUserService, DownloadDaoService downloadDaoService) {
        super("download", "command for download by magnet uri");
        this.telegramUserService = telegramUserService;
        this.downloadDaoService = downloadDaoService;
    }

    //todo: spagetti code - need refactoring
    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        //move to start command.
        //if user don;t start don't download until
        TelegramUser telegramUser = telegramUserService.findUserByName(user.getUserName());
        if (telegramUser == null) {
            try {
                SendMessage sendMessage = new SendMessage(chat.getId().toString(), "You are not found. Do /start before using bot.");
                telegramClient.execute(sendMessage);
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return;
            }
        }

        //send business event to download service

        //strings -> arguments with magnet link
        //TODO: don't download here - start async somewhere else
        if (strings.length == 1) { //wait only one magnet link
            //save downloads to db
            Download download = Download.builder()
                    .telegramUser(telegramUser)
                    .magnetUrl(strings[0])
                    .downloadStatus(DownloadStatus.NEW)
                    .build();
            downloadDaoService.save(download);
        } else {
            throw new RuntimeException("More than 1 argument. Stop.");
        }
    }

    //consume business event that download is compete - and start sending to user file from s3
}
