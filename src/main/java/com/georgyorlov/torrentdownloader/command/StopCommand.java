package com.georgyorlov.torrentdownloader.command;

import com.georgyorlov.torrentdownloader.user.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Component
public class StopCommand extends BotCommand {

    private final TelegramUserService telegramUserService;

    public StopCommand(TelegramUserService telegramUserService) {
        super("stop", "delete info about user and chat");
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        telegramUserService.deleteUserByName(user.getUserName());
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chat.getId().toString());
            sendMessage.setText("Bye bye");
            absSender.execute(sendMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
