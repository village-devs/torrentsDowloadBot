package com.georgyorlov.torrentdownloader.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "telegram_users")
public class TelegramUser {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String name;
    @Column
    private Long chatId;

}
