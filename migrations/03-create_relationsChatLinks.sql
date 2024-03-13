create table chat_link_relation
(
    chat_id bigint not null,
    link text not null,
    primary key (chat_id, link),
    foreign key (chat_id) references chats (telegram_chat_id),
    foreign key (link) references links (link)
);
