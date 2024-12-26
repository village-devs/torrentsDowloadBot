package com.georgyorlov.torrentdownloader.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    @Transactional
    public void createAndSaveUser(String username, Long chatId) {
        telegramUserRepository.save(TelegramUser.builder()
                .name(username)
                .chatId(chatId)
                .build());
    }

    @Transactional(readOnly = true)
    public TelegramUser findUserByName(String name) {
        return telegramUserRepository.findByName(name);
    }

    @Transactional
    public void deleteUserByName(String name) {
        telegramUserRepository.deleteByName(name);
    }
}
