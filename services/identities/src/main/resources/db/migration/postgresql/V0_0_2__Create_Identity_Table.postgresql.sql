CREATE TABLE IF NOT EXISTS identity (
    id                      bigserial   not null unique primary key,
    username                text        not null unique,
    password                text        not null,
    authorities             text[]      not null,
    expired                 boolean     not null,
    locked                  boolean     not null,
    credentials_expired     boolean     not null,
    disabled                 boolean     not null
);
