create table if not exists user(
id int not null primary key auto_increment,
openid varchar(128),
username varchar(20),
department varchar(20),
staffNumber varchar(20),
office varchar(20),
email varchar(50),
bindFlag varchar(20),
avatarUrl varchar(512),
city varchar(20),
gender varchar(10),
language varchar(10),
nikeName varchar(20),
province varchar(10)
);

create table if not exists ticket(
id int not null primary key auto_increment,
staffNumber varchar(20) not null,
bookTime timestamp,
useDate varchar(10),
useTime varchar(10),
usedFlag varchar(10)
/* foreign key(staffNumber) references user(staffNumber)*/
);

create table if not exists password_email(
staffNumber varchar(20) not null primary key,
password varchar(20),
sendTime timestamp,
);




create index if not exists idx_user_openid on user(openid);
create unique index if not exists idx_user_username on user(department, office, username);
create unique index if not exists idx_user_staffName on user(staffNumber);

create unique index if not exists idx_ticket_useTime on ticket(staffNumber, useDate, useTime);

create index if not exists idx_password_email_sendTime on password_email(sendTime, password);

