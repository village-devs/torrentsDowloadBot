package com.georgyorlov.torrentdownloader.download;

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
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class DownloadService {
    // дубли
    // задачи "в процессе"

    private static final long MAX_SIZE = 3l * 1024 * 1024 * 1024;
    private static final String DEFAULT_FOLDER = "./files/";
    private final DownloadDaoService downloadDaoService;
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

    @Scheduled(cron = "0 */5 * * * *")
    private void download() {
        Download download = downloadDaoService.findFirstDownload();

    }

    //consume business event for downloading torrents
    //save file on s3 and send bsiness event for download complete(or failed + timeout)
    private void download(Download download) {
        String magnetUrl = download.getMagnetUrl();
        Path targetDirectory = Paths.get(DEFAULT_FOLDER);//./files -> volume outside of docker container
        Storage storage = new FileSystemStorage(targetDirectory);

        BtClient client = Bt.client(runtime)
                .storage(storage)
                .magnet(magnetUrl)
                //.afterTorrentFetched(torrent -> afterFetched(torrent, magnetUrl))
                .afterDownloaded(torrent -> afterDownload(torrent, download))
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
                    state.getConnectedPeers().stream()
                            .map(k -> k.getPeer().getInetAddress().getHostAddress())
                            .reduce("", (s, inetAddress) -> s + " " + inetAddress)));
        }, 5000);
    }

    private void afterDownload(Torrent torrent, Download download) {
        // How to send a folder to client?
        // if you put dir into telegram its make a zip file from that dir
        final List<TorrentFile> files = torrent.getFiles();
        download.getTelegramUser().getChatId();////
        files.forEach(torrentFile -> {
            final List<String> pathElements = torrentFile.getPathElements();
            //for one file. if there are dirs -> fix me
            //try - catch to delete all context
            File file = new File(DEFAULT_FOLDER + pathElements.stream().findFirst().get());
            //check file exist (dir or not, one file or not)
/*            InputFile inputFile = new InputFile();
            inputFile.setMedia(file);
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chat.getId());
            sendDocument.setDocument(inputFile);*/
            /*File file = new File("./files/"
                                     + torrent.getName()
                                     + "/"
                                     + pathElements.stream().reduce((s, s1) -> s + "/" + s1).orElse(""));
            InputFile inputFile = new InputFile();
            inputFile.setMedia(file);
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chat.getId());
            sendDocument.setDocument(inputFile);*/
/*
            try {
                absSender.execute(sendDocument);
            } catch (TelegramApiException exception) {
                throw new RuntimeException(exception);
            } finally {
                file.delete();
                //clients.get(torrent)
            }*/
        });
    }

    private void afterFetched(Torrent torrent, String magnetUrl) {
        if (torrent.getSize() > MAX_SIZE) { //more that 3GB -> toggle
           /* try {
                absSender.execute(new SendMessage() {{
                    setChatId(chat.getId());
                    setText("Error. File is too big. No more that 3GB allowed. Stop downloading.");
                }});
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }*/
            //BtClient btClient = clients.get(magnetUrl);
            //btClient.stop();
            /*SendMessage message = new SendMessage();
            message.setText("Error. File is too big. No more that 3GB allowed. Stop downloading.");
            message.setChatId(chat.getId());
            try {
                absSender.execute(message);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
            throw new IllegalStateException("Error. File is too big. No more that 3GB allowed. Stop downloading.");*/
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
