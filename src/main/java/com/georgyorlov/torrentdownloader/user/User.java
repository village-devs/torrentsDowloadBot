package com.georgyorlov.torrentdownloader.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String telegramUsername;
    @Column
    private Long telegramChatId;
    @Column(length = 65535)//TODO: migrations add
    private String magnetUri;

    @ElementCollection
    private List<String> magnetUris = new ArrayList<>();

}
