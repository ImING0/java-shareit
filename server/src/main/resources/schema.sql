drop table if exists USERS cascade;
drop table if exists ITEMS cascade;
drop table if exists BOOKINGS cascade;
drop table if exists COMMENTS cascade;
drop table if exists REQUESTS cascade;

create table USERS
(
    ID    BIGSERIAL,
    NAME  CHARACTER VARYING(73)  not null,
    EMAIL CHARACTER VARYING(260) not null
        constraint "USERS_pk_email"
            unique,
    constraint "USERS_pk_id"
        primary key (ID)
);

create table ITEMS
(
    ID          BIGSERIAL,
    OWNER_ID    BIGINT                  not null,
    NAME        CHARACTER VARYING(128)  not null,
    DESCRIPTION CHARACTER VARYING(1024) not null,
    AVAILABLE   BOOLEAN                 not null,
    REQUEST_ID  BIGINT,
    constraint "ITEM_pk_id"
        primary key (ID),
    constraint "ITEMS_OWNER_ID_USER_ID_fk"
        foreign key (OWNER_ID) references USERS
            on update cascade on delete cascade
);

create table COMMENTS
(
    ID           BIGSERIAL,
    TEXT         VARCHAR(6666)               not null,
    ITEM_ID      BIGINT                      not null,
    AUTHOR_ID    BIGINT                      not null,
    CREATED_DATE TIMESTAMP WITHOUT TIME ZONE not null,
    constraint "COMMENTS_ID_pk"
        primary key (ID),
    constraint "COMMENTS_ITEM_ID_fk"
        foreign key (ITEM_ID) references ITEMS
            on update cascade on delete cascade,
    constraint "COMMENTS_USER_ID_fk"
        foreign key (AUTHOR_ID) references USERS
            on update cascade on delete cascade
);

create table BOOKINGS
(
    ID         BIGSERIAL,
    ITEM_ID    BIGINT    not null,
    BOOKER_ID  BIGINT,
    STATUS     VARCHAR(10) NOT NULL DEFAULT 'WAITING',
    START_DATE TIMESTAMP not null,
    END_DATE   TIMESTAMP not null,
    constraint "BOOKINGS_pk_ID"
        primary key (ID),
    constraint "BOOKINGS_BOOKER_ID_fk"
        foreign key (BOOKER_ID) references USERS
            on update cascade on delete cascade,
    constraint "BOOKINGS_ITEM_ID_fk"
        foreign key (ITEM_ID) references ITEMS
            on update cascade on delete cascade
);

create table REQUESTS
(
    ID          BIGSERIAL,
    DESCRIPTION VARCHAR(400)                not null,
    REQUESTOR_ID   BIGINT                      not null,
    CREATED     TIMESTAMP WITHOUT TIME ZONE not null,
    constraint "REQUESTS_ID_pk"
        primary key (ID),
    constraint "REQUESTS_REQUESTOR_ID_fk"
        foreign key (REQUESTOR_ID) references USERS
);
