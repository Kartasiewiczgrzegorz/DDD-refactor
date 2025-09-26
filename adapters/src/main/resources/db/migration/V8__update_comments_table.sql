alter table COMMENTS add column user_id int null;
alter table COMMENTS add foreign key (user_id) references USERS(ID);