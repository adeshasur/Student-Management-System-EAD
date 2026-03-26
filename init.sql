-- H2 Database Initialization Script
-- Run Mode: MySQL Compatible

DROP TABLE IF EXISTS marks;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS subject;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS class;

CREATE TABLE class (
    cid INT PRIMARY KEY AUTO_INCREMENT,
    classname VARCHAR(255) NOT NULL,
    section VARCHAR(255) NOT NULL
);

INSERT INTO class (classname, section) VALUES ('1', 'A');
INSERT INTO class (classname, section) VALUES ('1', 'B');
INSERT INTO class (classname, section) VALUES ('1', 'C');
INSERT INTO class (classname, section) VALUES ('2', 'A');
INSERT INTO class (classname, section) VALUES ('2', 'B');
INSERT INTO class (classname, section) VALUES ('2', 'C');
INSERT INTO class (classname, section) VALUES ('3', 'A');
INSERT INTO class (classname, section) VALUES ('3', 'B');
INSERT INTO class (classname, section) VALUES ('3', 'C');

CREATE TABLE subject (
    sid INT PRIMARY KEY AUTO_INCREMENT,
    subjectname VARCHAR(255) NOT NULL
);

INSERT INTO subject (subjectname) VALUES ('Software Eng.');
INSERT INTO subject (subjectname) VALUES ('Network Eng.');

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone INT NOT NULL,
    address VARCHAR(255) NOT NULL,
    uName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    uType VARCHAR(255) NOT NULL
);

INSERT INTO user (name, phone, address, uName, password, uType) VALUES ('Amal Silva', 715689652, '56/8A Yakkala, Gampaha', 'admin', 'admin', 'Admin');
INSERT INTO user (name, phone, address, uName, password, uType) VALUES ('Kasuni', 4554555, 'SAdss', 'Kasuni', '1234', 'Teacher');

CREATE TABLE student (
    studentid INT PRIMARY KEY AUTO_INCREMENT,
    stname VARCHAR(255) NOT NULL,
    pname VARCHAR(255) NOT NULL,
    dob DATE,
    gender VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    class VARCHAR(255),
    section VARCHAR(255)
);

CREATE TABLE exam (
    examid INT PRIMARY KEY AUTO_INCREMENT,
    examname VARCHAR(255) NOT NULL,
    date DATE,
    class VARCHAR(255),
    section VARCHAR(255),
    subject VARCHAR(255)
);

CREATE TABLE marks (
    markid INT PRIMARY KEY AUTO_INCREMENT,
    stid INT,
    stname VARCHAR(255),
    class VARCHAR(255),
    subject VARCHAR(255),
    marks INT
);

