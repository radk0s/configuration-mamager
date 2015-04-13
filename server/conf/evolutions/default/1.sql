# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table TOKENS (
  uuid                      varchar(255) not null,
  email                     varchar(255),
  created_at                datetime,
  expire_at                 datetime,
  is_sign_up                tinyint(1) default 0,
  constraint pk_TOKENS primary key (uuid))
;

create table USERS (
  ID                        varchar(255) not null,
  EMAIL                     varchar(255),
  PASSWORD                  varchar(255),
  PROVIDER                  varchar(255),
  FIRST_NAME                varchar(255),
  LAST_NAME                 varchar(255),
  constraint pk_USERS primary key (ID))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table if EXISTS TOKENS;

drop table if EXISTS USERS;

SET FOREIGN_KEY_CHECKS=1;

