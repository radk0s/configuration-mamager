# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table configuration (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  data                      longtext,
  provider                  integer,
  user_id                   bigint,
  constraint ck_configuration_provider check (provider in (0,1)),
  constraint pk_configuration primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  EMAIL                     varchar(255),
  PASSWORD                  integer,
  PROVIDER                  integer,
  FIRST_NAME                varchar(255),
  LAST_NAME                 varchar(255),
  auth_token                integer,
  aws_secret_key            varchar(255),
  aws_access_key            varchar(255),
  digital_ocean_token       varchar(255),
  aws_private_key           longtext,
  aws_keypair_name          varchar(255),
  constraint ck_user_PROVIDER check (PROVIDER in (0,1)),
  constraint pk_user primary key (id))
;

alter table configuration add constraint fk_configuration_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_configuration_user_1 on configuration (user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table configuration;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

