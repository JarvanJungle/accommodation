create table if not exists youtravel_loaddata
(
    id                integer not null
        constraint youtravel_loaddata_pk
            primary key,
    last_hotel_update date
);

alter table youtravel_loaddata
    owner to postgres;

