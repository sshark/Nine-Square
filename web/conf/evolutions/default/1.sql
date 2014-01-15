# --- !Ups

create table user (
  oid bigint primary key auto_increment,
  email varchar(255) unique,
  password varchar(255),
  name varchar(255),
  date_created timestamp,
  active boolean);

create table puzzle (
  oid bigint primary key auto_increment,
  board varchar(255),
  level int,
  date_created timestamp);

create table achievement (
  oid bigint primary key auto_increment,
  user_oid bigint,
  puzzle_oid bigint,
  date_created timestamp,
  unique(user_oid, puzzle_oid),
  foreign key (user_oid) references user(oid),
  foreign key (puzzle_oid) references puzzle(oid));

# --- !Downs

drop table achievement;
drop table user;
drop table puzzle;
