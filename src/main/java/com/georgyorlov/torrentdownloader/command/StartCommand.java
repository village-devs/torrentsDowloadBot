package com.georgyorlov.torrentdownloader.command;

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
public class StartCommand extends BotCommand {

    private final TelegramUserService telegramUserService;

    public StartCommand(TelegramUserService telegramUserService) {
        super("start", "save user and chat_id for future downloads");
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        //check user existence

        //send data event to download and user services
        try {
            telegramUserService.createAndSaveUser(user.getId(), user.getUserName(), chat.getId());
            log.info("new user [{}] saved", user.getUserName());
            try {
                SendMessage sendMessage = new SendMessage(chat.getId().toString(), "Thanks and welcome to using bot.");

                telegramClient.execute(sendMessage);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                SendMessage sendMessage = new SendMessage(chat.getId().toString(), "Sorry an error has happened");
                telegramClient.execute(sendMessage);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
