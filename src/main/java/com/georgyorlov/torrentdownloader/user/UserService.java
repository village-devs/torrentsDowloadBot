package com.georgyorlov.torrentdownloader.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createAndSaveUser(String username, Long chatId, String magnet) {
        userRepository.save(User.builder()
                .telegramUsername(username)
                .telegramChatId(chatId)
                .magnetUri(magnet)
                .build());
    }
}
