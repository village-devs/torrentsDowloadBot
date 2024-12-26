package com.georgyorlov.torrentdownloader;

import com.georgyorlov.torrentdownloader.bot.TorrentsDownloadBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(
                () -> log.info("Total memory: {} bytes", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()),
                0,
                1,
                TimeUnit.MINUTES);

    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TorrentsDownloadBot torrentsDownloadBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(torrentsDownloadBot);
        return botsApi;
    }
}
