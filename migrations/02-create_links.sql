create table links
(
    link_id     uuid,
    link        text,
    last_update timestamp with time zone not null,
    last_check  timestamp with time zone not null,
    primary key (link_id)
);
