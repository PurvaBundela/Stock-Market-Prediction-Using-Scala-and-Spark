# User schema

# --- !Ups

#--- !Ups
create table `user` (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  age INT NOT NULL,
   email VARCHAR(255) NOT NULL,
  password VARCHAR(100) NOT NULL,
  UNIQUE (email)
);


#--- !Downs
drop table "user"