alter table USERS drop column login;
alter table USERS drop column password;
alter table USERS drop column email;
drop table if exists logins;
