package com.georgyorlov.torrentdownloader.common.exception;

public class UserNotFoundException extends TorrentDownloadBotException {
    public UserNotFoundException() {
        super("User not found");
    }
}