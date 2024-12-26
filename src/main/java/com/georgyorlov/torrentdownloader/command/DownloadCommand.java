package com.georgyorlov.torrentdownloader.command;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.BtRuntimeBuilder;
import bt.runtime.Config;
import com.georgyorlov.torrentdownloader.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
public class DownloadCommand extends BotCommand {

    private final UserService userService;

    public DownloadCommand(UserService userService) {
        super("download", "command for download by magnet uri");
        this.userService = userService;
    }

    private static final long MAX_SIZE = 3l * 1024 * 1024 * 1024;
    //private final ConcurrentMap<String, BtClient> clients = new ConcurrentHashMap();
    private final BtRuntime runtime = new BtRuntimeBuilder()
            .config(new Config() {
                @Override
                public int getNumOfHashingThreads() {
                    return 2;//Runtime.getRuntime().availableProcessors();
                }
            })
            .module(new DHTModule(new DHTConfig() {
                @Override
                public boolean shouldUseRouterBootstrap() {
                    return true;
                }
            }))
            .autoLoadModules()
            .build();


    //todo: spagetti code - need refactoring
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        userService.createAndSaveUser(user.getUserName(), chat.getId(), Arrays.stream(strings).collect(Collectors.joining(";;;")));
        log.info(" {} - {}", user.getUserName(), chat.getId().toString());
        //strings -> arguments with magnet link
        //TODO: don't download here - start async somewhere else
        if (strings.length == 1) { //wait only one magnet link
            String magnetUrl = strings[0];
            Path targetDirectory = Paths.get("");//./files -> volume outside of docker container
            Storage storage = new FileSystemStorage(targetDirectory);

            BtClient client = Bt.client(runtime)
                    .storage(storage)
                    .magnet(magnetUrl)
                    .afterTorrentFetched(torrent -> afterFetched(absSender, chat, torrent, magnetUrl))
                    .afterDownloaded(torrent -> afterDownload(absSender, chat, torrent))
                    .stopWhenDownloaded()
                    .build();

            //clients.put(magnetUrl, client);

            client.startAsync(state -> {
                boolean complete = (state.getPiecesRemaining() == 0);
                if (complete) {
               /* if (options.shouldSeedAfterDownloaded()) {
                    System.out.println("download is complete");;
                } else {*/
                    System.out.println("stop client");//how to stop?
                    //client.stop();
                    //clients.remove(magnetUrl);
                    //}
                }
                System.out.println(format("This: %s, Remain %s, Downloaded %s. Peers connected %s",
                        client.toString(),
                        state.getPiecesRemaining(),
                        state.getDownloaded(),
                        state.getConnectedPeers().stream().map(k -> k.getPeer().getInetAddress().getHostAddress()).reduce("", (s, inetAddress) -> s + " " + inetAddress)));
            }, 5000);
        } else {
            throw new RuntimeException("More than 1 argument. Stop.");
        }
    }

    private void afterDownload(AbsSender absSender, Chat chat, Torrent torrent) {
        // How to send a folder to client?
        // if you put dir into telegram its make a zip file from that dir
        final List<TorrentFile> files = torrent.getFiles();
        files.forEach(torrentFile -> {
            final List<String> pathElements = torrentFile.getPathElements();
            File file = new File(pathElements.stream().findFirst().get());
            //check file exist (dir or not, one file or not)
            InputFile inputFile = new InputFile();
            inputFile.setMedia(file);
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chat.getId());
            sendDocument.setDocument(inputFile);
            /*File file = new File("./files/"
                                     + torrent.getName()
                                     + "/"
                                     + pathElements.stream().reduce((s, s1) -> s + "/" + s1).orElse(""));
            InputFile inputFile = new InputFile();
            inputFile.setMedia(file);
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chat.getId());
            sendDocument.setDocument(inputFile);*/

            try {
                absSender.execute(sendDocument);
            } catch (TelegramApiException exception) {
                throw new RuntimeException(exception);
            } finally {
                file.delete();
                //clients.get(torrent)
            }
        });
    }

    private void afterFetched(AbsSender absSender, Chat chat, Torrent torrent, String magnetUrl) {
        if (torrent.getSize() > MAX_SIZE) { //more that 3GB -> toggle
            try {
                absSender.execute(new SendMessage() {{
                    setChatId(chat.getId());
                    setText("Error. File is too big. No more that 3GB allowed. Stop downloading.");
                }});
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
            //BtClient btClient = clients.get(magnetUrl);
            //btClient.stop();
            SendMessage message = new SendMessage();
            message.setText("Error. File is too big. No more that 3GB allowed. Stop downloading.");
            message.setChatId(chat.getId());
            try {
                absSender.execute(message);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
            throw new IllegalStateException("Error. File is too big. No more that 3GB allowed. Stop downloading.");
        }
        /*if (torrent.getFiles().size() > 1) {
            BtClient btClient = clients.get(magnetUrl);
            btClient.stop();
            SendMessage message = new SendMessage();
            message.setText("Error. More than 1 file in torrent.");
            message.setChatId(chat.getId());
            try {
                absSender.execute(message);
            } catch (TelegramApiException ex) {
                //todo
            }
            throw new IllegalStateException("Error. More than 1 file in torrent.");
        }*/
    }
}
