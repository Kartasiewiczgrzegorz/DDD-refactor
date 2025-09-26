create table logins(
                      id int primary key auto_increment,
                      nick varchar(20) not null,
                      password varchar(20) not null,
                      email varchar(50) not null,
                      user_id int not null,
                      foreign key (user_id) references USERS(ID)
)