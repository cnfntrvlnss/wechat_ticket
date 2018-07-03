create table if not exists user(
id int not null primary key auto_increment,
openid varchar(128),
username varchar(20),
department varchar(20),
staffNumber varchar(20),
office varchar(20),
email varchar(20),
password varchar(20),
avatarUrl varchar(512),
city varchar(20),
gender varchar(10),
language varchar(10),
nikeName varchar(20),
province varchar(10)
);

create table if not exists ticket(
id int not null primary key,
staffNumber varchar(20) not null,
bookTime timestamp,
useDate varchar(10),
useTime varchar(10),
usedFlag varchar(10)
/* foreign key(staffNumber) references user(staffNumber)*/
);

create index if not exists idx_user_openid on user(openid);
create index if not exists idx_ticket_useTime on ticket(useDate, useTime);
create unique index if not exists idx_user_username on user(department, office, username);
create unique index if not exists idx_user_staffName on user(staffNumber);