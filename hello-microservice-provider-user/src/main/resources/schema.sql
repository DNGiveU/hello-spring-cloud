drop table user if exists;
create table user (
	id bigint generated by default as identity, 
	username varchar(255), 
	age int, 
	primary key (id)
);