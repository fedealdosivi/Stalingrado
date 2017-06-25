create database batallas

use batallas

create table informes(
id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
resultado varchar(200)
);

drop table informes;

select * from informes;

insert into informes(id,resultado)values('1',"gano urss");