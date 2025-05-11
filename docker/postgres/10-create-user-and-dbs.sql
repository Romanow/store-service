-- file: 10-create-user-and-dbs.sql
CREATE USER program WITH PASSWORD 'test';
CREATE DATABASE store OWNER program;
CREATE DATABASE warehouse OWNER program;
CREATE DATABASE warranty OWNER program;
CREATE DATABASE keycloak OWNER program;
