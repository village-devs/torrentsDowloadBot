package com.georgyorlov.torrentdownloader;

import com.georgyorlov.torrentdownloader.bot.TorrentsDownloadBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(Application.class, args);
/*
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(new TorrentsDownloadBot());*/

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(
                () -> log.info("Total memory: {} bytes", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()),
                0,
                1,
                TimeUnit.MINUTES);

    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApi(TorrentsDownloadBot torrentsDownloadBot) throws TelegramApiException {
        TelegramBotsLongPollingApplication botsApi = new TelegramBotsLongPollingApplication();
        botsApi.registerBot(System.getenv("botToken"), torrentsDownloadBot);
        return botsApi;
    }
}
