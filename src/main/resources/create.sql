/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  altmf
 * Created: 30.09.2017
 */

CREATE USER IF NOT EXISTS LOCAL_ADMIN PASSWORD 123 ADMIN;

DROP SCHEMA IF EXISTS BEA;

CREATE SCHEMA IF NOT EXISTS BEA;

CREATE ROLE IF NOT EXISTS ALL_RIGHT;

GRANT ALL_RIGHT TO LOCAL_ADMIN;

GRANT ALTER ANY SCHEMA TO LOCAL_ADMIN;

SET SCHEMA BEA;

-------------------------------РОЛИ-------------------------------------------------
CREATE TABLE BEA.CLS_USER(
    ID              BIGINT IDENTITY,
    ID_employee     BIGINT,
    IS_DELETED      INT DEFAULT 0,
    LOGIN           VARCHAR(255),
    PASSWORD        BLOB
);

CREATE TABLE BEA.CLS_ROLE(
    ID              BIGINT IDENTITY,
    IS_DELETED      INT DEFAULT 0,
    NAME            VARCHAR(255),
    CODE            VARCHAR(255)
);

CREATE TABLE BEA.CLS_RESOURCE(
    ID              BIGINT IDENTITY,
    IS_DELETED      INT DEFAULT 0,
    NAME            VARCHAR(255),
    PATH            VARCHAR(255),
    CODE            VARCHAR(255)
);

CREATE TABLE BEA.CLS_CVITANTION(
    ID              BIGINT IDENTITY,
    ID_RESOURCE     BIGINT NOT NULL,
    ID_ROLE         BIGINT NOT NULL,
    IS_DELETED      INT DEFAULT 0,
    OPERATION       VARCHAR(4)
);

CREATE TABLE BEA.REG_USE_ROLE(
    ID              BIGINT IDENTITY,
    ID_ROLE         BIGINT NOT NULL,
    IS_DELETED      INT DEFAULT 0,
    NAME            VARCHAR(255)
);
--------------------------------------------------------------------------------
CREATE TABLE BEA.CLS_employee(
    ID              BIGINT IDENTITY,
    IS_DELETED      INT DEFAULT 0,
    FAM             VARCHAR(255),
    IM              VARCHAR(255),
    OTC             VARCHAR(255)
);

CREATE TABLE BEA.CLS_CUSTOMER(
    ID              BIGINT IDENTITY,
    IS_DELETED      INT DEFAULT 0,
    FAM             VARCHAR(255),
    IM              VARCHAR(255),
    OTC             VARCHAR(255)
);

CREATE TABLE BEA.REG_CUSTOMER_CONTACT(
    ID              BIGINT IDENTITY,
    ID_CUSTOMER     BIGINT NOT NULL,
    ID_CONTACT_TYPE BIGINT,
    IS_DELETED      INT DEFAULT 0,
    TEXTVALUE       VARCHAR(255),
    FOREIGN KEY(ID_CUSTOMER) REFERENCES CLS_CUSTOMER(ID)
);

CREATE TABLE BEA.CLS_SERVICE(
    ID              BIGINT IDENTITY,
    IS_DELETED      INT DEFAULT 0,
    NAME            VARCHAR(255),
    APR_DURATION    DECIMAL(5,2)
);

CREATE TABLE BEA.REG_SCHEDULE(
    ID              BIGINT IDENTITY,
    ID_CUSTOMER     BIGINT,
    ID_employee     BIGINT,
    ID_SERVICE      BIGINT,
    IS_DELETED      INT DEFAULT 0,
    DATE_REG        TIMESTAMP,
    DATE_TIME_SERVICE_BEGIN   TIMESTAMP,
    DATE_TIME_SERVICE_END     TIMESTAMP,
);