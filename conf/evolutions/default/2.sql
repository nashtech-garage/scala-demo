
-- !Ups

create table scalademo.users
(
    id         serial       not null constraint users_pk primary key,
    name       varchar(64)  not null,
    lastName   varchar(64)  not null,
    password   varchar(128) not null,
    email      varchar(100) not null,
    role       varchar(16)  not null
);

create unique index users_id_uindex
    on scalademo.users (id);

create unique index users_email_uindex
    on scalademo.users (email);

create table scalademo.posts
(
	id              serial          not null constraint posts_pk primary key,
	author          serial          not null,
	title           varchar(128)    not null,
	description     varchar(500),
	content         text            not null,
	date            timestamp       not null
);

create unique index posts_id_uindex
    on scalademo.posts (id);

-- !Downs

DROP TABLE scalademo.users;
DROP TABLE scalademo.posts;
