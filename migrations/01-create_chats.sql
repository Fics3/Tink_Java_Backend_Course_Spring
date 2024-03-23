create table chats
(
    telegram_chat_id bigint                   not null,
    created_at       timestamp with time zone not null,
    primary key (telegram_chat_id),
    unique (telegram_chat_id)
);
