# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table TOKENS (
  id                        bigint auto_increment not null,
  uuid                      varchar(255),
  email                     varchar(255),
  created_at                datetime,
  expire_at                 datetime,
  is_sign_up                tinyint(1) default 0,
  constraint pk_TOKENS primary key (id))
;

create table USERS (
  id                        bigint auto_increment not null,
  EMAIL                     varchar(255),
  PASSWORD                  integer,
  PROVIDER                  varchar(255),
  FIRST_NAME                varchar(255),
  LAST_NAME                 varchar(255),
  auth_token                integer,
  aws_token                 varchar(255),
  digital_ocean_token       varchar(255),
  constraint pk_USERS primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table TOKENS;

drop table USERS;

SET FOREIGN_KEY_CHECKS=1;

