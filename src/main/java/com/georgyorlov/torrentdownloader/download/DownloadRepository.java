package com.georgyorlov.torrentdownloader.download;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DownloadRepository extends JpaRepository<Download, Long> {

    Optional<Download> findFirstByDownloadStatus(DownloadStatus downloadStatus);
}
