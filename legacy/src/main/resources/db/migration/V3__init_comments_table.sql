drop table if exists comments;
create table comments(
    id int primary key auto_increment,
    time datetime not null,
    description varchar(200) not null
)