drop table if exists posts;
create table posts(
    id int primary key auto_increment,
    time datetime not null,
    description varchar(200) not null
)