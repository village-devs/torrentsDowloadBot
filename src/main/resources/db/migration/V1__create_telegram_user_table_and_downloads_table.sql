CREATE TABLE IF NOT EXISTS telegram_users
(
    user_id   bigint primary key,
    user_name varchar(255) not null,
    chat_id   bigint       not null unique,
    status    varchar(127) not null
);

CREATE TABLE IF NOT EXISTS downloads
(
    id         bigint primary key generated always as identity,
    magnet_url text         not null,
    status     varchar(127) not null,
    user_id    bigint       not null
);
