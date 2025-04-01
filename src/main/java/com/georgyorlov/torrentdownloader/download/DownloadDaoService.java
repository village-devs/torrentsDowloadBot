package com.georgyorlov.torrentdownloader.download;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DownloadDaoService {

    private final DownloadRepository downloadRepository;

    @Transactional
    public Download save(Download download) {
        return downloadRepository.save(download);
    }

    public Download findFirstDownload() {
        return downloadRepository.findFirstByDownloadStatus(DownloadStatus.NEW).orElseThrow(() -> new RuntimeException("kek lol"));
    }
}
