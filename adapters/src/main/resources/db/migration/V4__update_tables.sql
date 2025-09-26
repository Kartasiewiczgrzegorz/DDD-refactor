alter table COMMENTS add column post_id int null;
alter table COMMENTS add foreign key (post_id) references POSTS(ID);
alter table POSTS add column user_id int null;
alter table POSTS add foreign key (user_id) references USERS(ID);