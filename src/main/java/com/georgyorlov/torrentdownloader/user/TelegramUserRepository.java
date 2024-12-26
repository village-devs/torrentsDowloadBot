package com.georgyorlov.torrentdownloader.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    TelegramUser findByName(String name);

    void deleteByName(String name);
}
