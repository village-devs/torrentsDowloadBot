package com.georgyorlov.torrentdownloader.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    @Transactional
    public void createAndSaveUser(Long telegramUserId, String telegramUserName, Long telegramChatId) {
        telegramUserRepository.save(TelegramUser.builder()
                .userId(telegramUserId)
                .userName(telegramUserName)
                .chatId(telegramChatId)
                .userStatus(TelegramUser.UserStatus.ACTIVE)
                .build());
    }

    @Transactional
    public TelegramUser save(TelegramUser telegramUser) {
        return telegramUserRepository.save(telegramUser);
    }

    @Transactional(readOnly = true)
    public TelegramUser findUserByName(String name) {
        return telegramUserRepository.findByUserName(name);
    }

    @Transactional
    public void deleteUserByName(String name) {
        telegramUserRepository.deleteByUserName(name);
    }

}
