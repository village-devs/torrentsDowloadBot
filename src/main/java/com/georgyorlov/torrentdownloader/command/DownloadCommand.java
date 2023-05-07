package com.georgyorlov.torrentdownloader.command;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.TorrentFile;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.BtRuntimeBuilder;
import bt.runtime.Config;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

public class DownloadCommand extends BotCommand {

    private static final long MAX_SIZE = 3l * 1024 * 1024 * 1024;

    public DownloadCommand() {
        super("download", "command for download by magnet uri");
    }

    //todo: spagetti code - need refactoring
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        //strings -> arguments with magnet link
        if (strings.length == 1) { //wait only one magnet link
            Path targetDirectory = Paths.get("");
            Storage storage = new FileSystemStorage(targetDirectory);
            BtRuntime runtime = new BtRuntimeBuilder()
                    .config(new Config() {
                        @Override
                        public int getNumOfHashingThreads() {
                            return Runtime.getRuntime().availableProcessors();
                        }
                    })
                    .autoLoadModules()
                    .module(new DHTModule(new DHTConfig() {
                        @Override
                        public boolean shouldUseRouterBootstrap() {
                            return true;
                        }
                    }))
                    .build();

            BtClient client = Bt.client(runtime)
                    .storage(storage)
                    .magnet(strings[0])
                    .afterTorrentFetched(torrent -> {
                        if (torrent.getSize() > MAX_SIZE) { //more that 3GB
                            try {
                                absSender.execute(new SendMessage() {{
                                    setChatId(chat.getId());
                                    setText("Error. File is too big. No more that 3GB allowed. Stop downloading.");
                                }});
                            } catch (TelegramApiException ex) {
                                //todo
                            }
                            throw new IllegalStateException("Error. File is too big. No more that 3GB allowed. Stop downloading.");
                        }
                        if (torrent.getFiles().size() > 1) {
                            try {
                                absSender.execute(new SendMessage() {{
                                    setChatId(chat.getId());
                                    setText("Error. More than 1 file in torrent.");
                                }});
                            } catch (TelegramApiException ex) {
                                //todo
                            }
                            //how to stop download?
                            //client.stop();
                            throw new IllegalStateException("Error. More than 1 file in torrent.");
                        }
                    })
                    .afterDownloaded(torrent -> {
                        List<TorrentFile> files = torrent.getFiles();
                        //todo: if not one file -> zip all files and send
                        if (files.size() == 1) {
                            files.forEach(torrentFile -> {
                                List<String> pathElements = torrentFile.getPathElements();
                                if (pathElements.size() == 1) {
                                    File file = new File(pathElements.stream().findFirst().get());
                                    InputFile inputFile = new InputFile();
                                    inputFile.setMedia(file);
                                    SendDocument sendDocument = new SendDocument();
                                    sendDocument.setChatId(chat.getId());
                                    sendDocument.setDocument(inputFile);

                                    try {
                                        absSender.execute(sendDocument);
                                    } catch (TelegramApiException exception) {
                                        //todo
                                        file.delete();
                                    }
                                    file.delete();
                                } else {
                                    String fullPath = pathElements.stream().reduce((s, s2) -> s + " -> " + s2).get();
                                    throw new IllegalStateException(format("Subdirectories is not allowed %s", fullPath));
                                }
                            });
                        } else {
                            throw new IllegalStateException("More than 1 file in torrent. Stop.");
                        }
                    })
                    .stopWhenDownloaded()
                    .build();

            client.startAsync(state -> {
                boolean complete = (state.getPiecesRemaining() == 0);
                if (complete) {
               /* if (options.shouldSeedAfterDownloaded()) {
                    System.out.println("download is complete");;
                } else {*/
                    System.out.println("stop client");
                    client.stop();
                    //}
                }
                System.out.println(format("Remain %s, Downloaded %s. Peers connected %s", state.getPiecesRemaining(), state.getDownloaded(), state.getConnectedPeers().stream().map(k -> k.getPeer().getInetAddress().getHostAddress()).reduce("", (s, inetAddress) -> s + " " + inetAddress)));
            }, 1000).join();
        } else {
            throw new RuntimeException("More than 1 argument. Stop.");
        }

    }
}
