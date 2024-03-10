create table links
(
    link_id         bigint generated always as identity,
    created_at      timestamp with time zone not null,
    primary key (link_id)
);
