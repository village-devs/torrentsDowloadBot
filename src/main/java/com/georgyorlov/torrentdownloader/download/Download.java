package com.georgyorlov.torrentdownloader.download;

import com.georgyorlov.torrentdownloader.user.TelegramUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "downloads")
@AllArgsConstructor
@NoArgsConstructor
public class Download {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "magnet_url", nullable = false)
    private String magnetUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DownloadStatus downloadStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private TelegramUser telegramUser;
}
