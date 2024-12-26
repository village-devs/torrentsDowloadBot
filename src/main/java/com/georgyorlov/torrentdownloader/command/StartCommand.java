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
public class StartCommand extends BotCommand {

    private final TelegramUserService telegramUserService;

    public StartCommand(TelegramUserService telegramUserService) {
        super("start", "save user and chat_id for future downloads");
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        telegramUserService.createAndSaveUser(user.getUserName(), chat.getId());
        log.info("new user [{}] saved", user.getUserName());
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chat.getId());
            sendMessage.setText("Thanks and welcome to using bot.");
            absSender.execute(sendMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
