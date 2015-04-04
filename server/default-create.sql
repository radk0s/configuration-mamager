create table user (
  id                        bigint auto_increment not null,
  login                     varchar(255),
  password_md5              integer,
  constraint pk_user primary key (id))
;



