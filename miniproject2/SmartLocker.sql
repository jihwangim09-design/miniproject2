-- ============================================
-- SmartLocker 프로젝트 DB 전체 초기화
-- ============================================

DROP DATABASE IF EXISTS SmartLocker;
CREATE DATABASE SmartLocker;
USE SmartLocker;


-- ============================================
-- 1. 회원 테이블
-- ============================================
CREATE TABLE Users (
    u_no INT AUTO_INCREMENT,
    u_pwd VARCHAR(15) NOT NULL,
    u_phone VARCHAR(20) NOT NULL,
    u_name VARCHAR(20) NOT NULL,
    u_grade VARCHAR(10) DEFAULT 'user',
    u_student_id VARCHAR(20) NOT NULL UNIQUE,

    CONSTRAINT pk_users PRIMARY KEY (u_no)
);


-- ============================================
-- 2. 보관함 테이블
-- ============================================
CREATE TABLE Locker (
    l_no INT AUTO_INCREMENT,
    l_location VARCHAR(20) NOT NULL,
    l_status VARCHAR(10) DEFAULT '닫힘',

    CONSTRAINT pk_locker PRIMARY KEY (l_no)
);


-- ============================================
-- 3. 장비 테이블
-- ============================================
CREATE TABLE Equipment (
    e_no  int auto_increment ,
    e_name varchar(50) not null,
    e_category varchar(30),
    e_status varchar(20) DEFAULT '대여가능',
    l_no int,
    CONSTRAINT pk_equipment PRIMARY KEY (e_no)
);

create table Rental (
    r_no int auto_increment ,
    u_no int not null ,
    e_no int not null,
    r_date datetime not null default now(),
    r_due_date datetime not null DEFAULT (now() + interval 2 day),
    r_return_date datetime null ,
    r_status varchar(10) default '대여중',
    r_condition varchar(10) null,

    CONSTRAINT pk_rental PRIMARY KEY (r_no),

    CONSTRAINT fk_rental_users
        FOREIGN KEY (u_no)
        REFERENCES Users(u_no),

    CONSTRAINT fk_rental_equipment
        FOREIGN KEY (e_no)
        REFERENCES Equipment(e_no)
);

create table Report (
    report_id int auto_increment,
    r_no int not null,
    report_type varchar(20) not null,
    description text not null,
    created_at datetime default now(),
    status varchar(20) default '접수',

    constraint primary key (report_id),
    constraint foreign key (r_no) references rental (r_no)
);

-- user 테이블 샘플 데이터
DROP DATABASE IF EXISTS SmartLocker;
CREATE DATABASE SmartLocker;
USE SmartLocker;

-- 1. Users
CREATE TABLE Users (
  u_no int auto_increment,
  constraint primary key (u_no),
  u_pwd varchar(15) not null,
  u_phone varchar(20) not null,
  u_name varchar(20) not null,
  u_grade varchar(10) default 'user',
  u_student_id varchar(20) not null unique
);

-- 2. Locker
CREATE TABLE Locker (
    l_no int auto_increment,
    l_location varchar(20) not null,
    l_status varchar(10) DEFAULT '닫힘',
    PRIMARY KEY (l_no)
);

-- 3. Equipment
CREATE TABLE Equipment (
    e_no int auto_increment,
    e_name varchar(50) not null,
    e_category varchar(30),
    e_status varchar(20) DEFAULT '대여가능',
    l_no int,
    CONSTRAINT pk_equipment PRIMARY KEY (e_no),
    CONSTRAINT foreign key (l_no) references Locker (l_no)
);

-- 4. Rental
CREATE TABLE Rental (
    r_no int auto_increment,
    u_no int not null,
    e_no int not null,
    r_date datetime not null default now(),
    r_due_date datetime not null,
    r_return_date datetime null,
    r_status varchar(10) default '대여중',
    r_condition varchar(10) null,
    constraint primary key (r_no),
    constraint foreign key (u_no) references Users (u_no),
    constraint foreign key (e_no) references Equipment (e_no)
);

-- 5. Report
CREATE TABLE Report (
    report_id int auto_increment,
    r_no int not null,
    report_type varchar(20) not null,
    description text not null,
    created_at datetime default now(),
    status varchar(20) default '접수',
    constraint primary key (report_id),
    constraint foreign key (r_no) references Rental (r_no)
);
