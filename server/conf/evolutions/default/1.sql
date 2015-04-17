# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table TOKENS (
  id                        bigint auto_increment not null,
  uuid                      varchar(255),
  email                     varchar(255),
  created_at                timestamp,
  expire_at                 timestamp,
  is_sign_up                boolean,
  constraint pk_TOKENS primary key (id))
;

create table USERS (
  id                        bigint auto_increment not null,
  EMAIL                     varchar(255),
  PASSWORD                  integer,
  PROVIDER                  varchar(255),
  FIRST_NAME                varchar(255),
  LAST_NAME                 varchar(255),
  auth_token                varchar(255),
  constraint pk_USERS primary key (id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists TOKENS;

drop table if exists USERS;

SET REFERENTIAL_INTEGRITY TRUE;

