-- script for creating USERS table
create table USERS
(
  USER_LOGIN       VARCHAR2(100)          not null,
  USER_PASSWORD    VARCHAR2(50)           not null,
  USER_EMAIL       VARCHAR2(100)          not null,
  USER_STATUS      NUMBER,
  USER_SECNAME     VARCHAR2(100)          not null,
  USER_PHONENUMBER VARCHAR2(50)           not null,
  USER_FIRSTNAME   VARCHAR2(100)          not null,
  USER_BIRTH       DATE,
  USER_ID          NUMBER default 0000000 not null
)
/

create unique index USERS_USER_LOGIN_UINDEX
  on USERS (USER_LOGIN)
/

create unique index USERS_USER_ID_UINDEX
  on USERS (USER_ID)
/

alter table USERS
  add constraint USERS_USER_ID_PK
primary key (USER_ID)
/
-- Here we create sequence for auto increment
create sequence AUTO_INC_USER_ID
  start with 1000001
  increment by 1
  nocache
/
-- Here we create a trigger which will increment user_id by one after inserting in table.
create or replace trigger TR_AUTO_INC
  before insert
  on USERS
  for each row
  begin
    select auto_inc_user_id.nextval
      into :new.USER_ID
      from dual;
  end;
----------------------------------------- END OF CREATING USERS TABLE.
/
----------------------------------------- START OF CREATING CHATS TABLE.
create table CHATS
(
  CHAT_NAME VARCHAR2(100)          not null,
  CHAT_ID   NUMBER default 1000000 not null,
  ISPRIVATE NUMBER default 0       not null
)
/

create unique index CHATS_CHAT_ID_UINDEX
  on CHATS (CHAT_ID)
/

alter table CHATS
  add constraint CHATS_CHAT_ID_PK
primary key (CHAT_ID)
/
create sequence CHAT_AUTO_INC
  start with 1000001
  increment by 1
  nocache
/
create trigger CHAT_TRG_AUTO_INC
  before insert
  on CHATS
  for each row
  BEGIN
    SELECT chat_auto_inc.nextval
    INTO :new.CHAT_ID
    FROM dual;
  END;
/
----------------------------------------- END OF CREATING CHATS TABLE.

----------------------------------------- START OF CREATING CHAT_MESSAGES TABLE.
create table CHAT_MESSAGES
(
  MESSAGE_ID  NUMBER                    not null
    primary key,
  CHAT_ID     NUMBER                    not null
    constraint CHAT_MSG_CHAT_ID_FK
    references CHATS,
  MSG_AUTHOR  VARCHAR2(100)             not null,
  MSG_DATE    TIMESTAMP(6) default NULL not null,
  MSG_CONTENT VARCHAR2(2000)            not null
)
/
create sequence CHAT_MSG_AUTO_INC
  start with 1000001
  increment by 1
  nocache
/
create trigger CHAT_MSG_AUTO_TRG
  before insert
  on CHAT_MESSAGES
  for each row
  BEGIN
    SELECT chat_msg_auto_inc.nextval
    INTO :new.MESSAGE_ID
    FROM dual;
  END;
/
----------------------------------------- END OF CREATING CHAT_MESSAGES TABLE.

----------------------------------------- START OF CREATING CHAT_USERS TABLE.
create table CHAT_USERS
(
  CHAT_ID NUMBER default NULL not null
    constraint CHATUSERS_FK_CHAT_ID
    references CHATS,
  USER_ID NUMBER default NULL not null
    constraint CHATUSERS_FK_USER_ID
    references USERS
)
/
----------------------------------------- END OF CREATING CHAT_USERS TABLE.
