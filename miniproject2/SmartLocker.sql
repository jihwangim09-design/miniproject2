-- ============================================
-- SmartLocker 프로젝트 DB 전체 초기화
-- ============================================

-- ============================================
-- 1. 회원 테이블
-- ============================================


CREATE TABLE Users (
    u_no INT AUTO_INCREMENT,
    u_pwd VARCHAR(15) NOT NULL,
    u_phone VARCHAR(20) NOT NULL,
    u_name VARCHAR(20) NOT NULL,
    u_grade VARCHAR(10) NOT NULL DEFAULT 'user',
    u_student_id VARCHAR(20) NOT NULL UNIQUE,

    CONSTRAINT pk_users PRIMARY KEY (u_no),
    CONSTRAINT chk_users_grade CHECK (u_grade IN ('user', 'admin'))
);


-- ============================================
-- 2. 보관함 테이블
-- ============================================

CREATE TABLE Locker (
    l_no INT AUTO_INCREMENT,
    l_location VARCHAR(30) NOT NULL,
    l_status VARCHAR(10) NOT NULL DEFAULT '닫힘',

    CONSTRAINT pk_locker PRIMARY KEY (l_no),
    CONSTRAINT chk_locker_status CHECK (l_status IN ('열림', '닫힘'))
);


-- ============================================
-- 3. 장비 테이블
-- ============================================

CREATE TABLE Equipment (
    e_no INT AUTO_INCREMENT,
    e_name VARCHAR(50) NOT NULL,
    e_category VARCHAR(30) NOT NULL,
    e_status VARCHAR(20) NOT NULL DEFAULT '대여가능',
    l_no INT NOT NULL,

    CONSTRAINT pk_equipment PRIMARY KEY (e_no),

    -- 하나의 보관함에는 하나의 장비만 등록
    CONSTRAINT uk_equipment_locker UNIQUE (l_no),

    -- 존재하는 보관함만 등록 가능
    CONSTRAINT fk_equipment_locker
        FOREIGN KEY (l_no) REFERENCES Locker(l_no)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_equipment_status
       CHECK (e_status IN ('대여가능', '대여중', '고장', '파손', '점검필요', '점검중', '수리중', '사용중지'))
);


-- ============================================
-- 4. 대여 테이블
-- ============================================

CREATE TABLE Rental (
    r_no INT AUTO_INCREMENT,
    u_no INT NOT NULL,
    e_no INT NOT NULL,
    r_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    r_due_date DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 2 DAY),
    r_return_date DATETIME NULL,
    r_status VARCHAR(10) NOT NULL DEFAULT '대여중',
    r_condition VARCHAR(10) NULL,

    CONSTRAINT pk_rental PRIMARY KEY (r_no),

    CONSTRAINT fk_rental_users
        FOREIGN KEY (u_no) REFERENCES Users(u_no)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_rental_equipment
        FOREIGN KEY (e_no) REFERENCES Equipment(e_no)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_rental_status
        CHECK (r_status IN ('대여중', '반납완료', '연체')),

    CONSTRAINT chk_rental_condition
    CHECK (r_condition IS NULL OR r_condition IN ('정상', '이상있음'))
);


-- ============================================
-- 5. 신고 테이블
-- ============================================

CREATE TABLE Report (
    report_id INT AUTO_INCREMENT,
    r_no INT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT '접수',

    CONSTRAINT pk_report PRIMARY KEY (report_id),

    CONSTRAINT fk_report_rental
        FOREIGN KEY (r_no) REFERENCES Rental(r_no)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_report_type
        CHECK (report_type IN ('고장', '파손')),

    CONSTRAINT chk_report_status
        CHECK (status IN ('접수', '점검중', '수리중', '처리완료'))
);


-- ============================================
-- 6. 회원 샘플 데이터
-- ============================================

INSERT INTO Users (u_no, u_pwd, u_phone, u_name, u_grade, u_student_id) VALUES
(1, '1234', '010-1234-5678', '관리자', 'admin', '1'),
(2, '141543', '010-1431-5345', '야스오', 'user', '2020121'),
(3, 'pw123', '010-4321-5678', '요네', 'user', '2020122'),
(4, 'pw432', '010-9876-1234', '탑베인', 'user', '2021120'),
(5, 'pw000', '010-5678-9876', '말파이트', 'user', '2021121'),
(6, 'pw001', '010-1212-3434', '나르', 'user', '2022120'),
(7, 'pw4321', '010-6666-7777', '티모', 'user', '2022121'),
(8, 'pw876', '010-7878-8787', '모르가나', 'user', '2023120'),
(9, 'pw555', '010-9988-7766', '쉬바나', 'user', '2023121'),
(10, 'pw789', '010-9000-0000', '아우렐리온 솔', 'user', '2024120');


-- ============================================
-- 7. 보관함 샘플 데이터
-- ============================================

INSERT INTO Locker (l_location, l_status) VALUES
('성결관B102-01', '닫힘'),
('성결관B102-02', '닫힘'),
('성결관B102-03', '닫힘'),
('성결관B102-04', '닫힘'),
('성결관B102-05', '닫힘'),
('성결관B102-06', '닫힘'),
('성결관B102-07', '닫힘'),
('성결관B102-08', '닫힘'),
('성결관B102-09', '닫힘'),
('성결관B102-10', '닫힘');


-- ============================================
-- 8. 장비 샘플 데이터
-- ============================================

INSERT INTO Equipment (e_name, e_category, e_status, l_no) VALUES
('LG그램', '노트북', '대여가능', 1),
('LG그램', '노트북', '대여중', 2),
('갤럭시탭 S9', '태블릿', '대여중', 3),
('갤럭시탭 S9', '태블릿', '파손', 4),
('Arduino UNO Kit', '아두이노', '대여가능', 5),
('Arduino UNO Kit', '아두이노', '수리중', 6),
('Raspberry Pi 5', '라즈베리파이', '대여가능', 7),
('미러리스 카메라', '카메라', '대여중', 8),
('빔프로젝터', '프로젝터', '대여중', 9),
('캡처보드', '캡처보드', '대여가능', 10);


-- ============================================
-- 9. 대여 샘플 데이터
-- ============================================

INSERT INTO Rental
(u_no, e_no, r_date, r_due_date, r_return_date, r_status, r_condition) VALUES
(2, 3, '2026-08-19 14:30:00', '2026-08-21 14:30:00', NULL, '대여중', NULL),
(4, 5, '2026-08-15 09:10:00', '2026-08-18 09:10:00', '2026-08-17 16:20:00', '반납완료', '정상'),
(6, 7, '2026-08-10 11:00:00', '2026-08-13 11:00:00', '2026-08-15 10:45:00', '반납완료', '고장'),
(8, 9, '2026-08-20 13:15:00', '2026-08-23 13:15:00', NULL, '대여중', NULL),
(10, 1, '2026-08-05 10:00:00', '2026-08-08 10:00:00', '2026-08-08 09:30:00', '반납완료', '정상'),
(3, 8, '2026-08-01 15:40:00', '2026-08-04 15:40:00', NULL, '연체', NULL),
(5, 4, '2026-08-12 08:50:00', '2026-08-15 08:50:00', '2026-08-14 17:00:00', '반납완료', '파손'),
(7, 2, '2026-08-18 16:05:00', '2026-08-21 16:05:00', NULL, '대여중', NULL),
(1, 10, '2026-07-28 12:20:00', '2026-07-31 12:20:00', '2026-08-03 11:10:00', '반납완료', '고장'),
(9, 6, '2026-08-14 09:45:00', '2026-08-17 09:45:00', '2026-08-16 14:00:00', '반납완료', '고장');


-- ============================================
-- 10. 신고 샘플 데이터
-- ============================================

INSERT INTO Report (r_no, report_type, description, status) VALUES
(3, '고장', '라즈베리파이 전원이 정상적으로 켜지지 않습니다.', '처리완료'),
(7, '파손', '태블릿 화면 모서리에 금이 발생했습니다.', '접수'),
(9, '고장', '캡처보드 HDMI 연결이 정상적으로 인식되지 않습니다.', '처리완료'),
(10, '고장', '아두이노 보드가 과열되고 탄 냄새가 발생합니다.', '수리중');


-- ============================================
-- 11. 전체 데이터 확인
-- ============================================

SELECT * FROM Users;
SELECT * FROM Locker;
SELECT * FROM Equipment;
SELECT * FROM Rental;
SELECT * FROM Report;