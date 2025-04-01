package com.georgyorlov.torrentdownloader.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    TelegramUser findByUserName(String userName);

    void deleteByUserName(String nickname);
}
