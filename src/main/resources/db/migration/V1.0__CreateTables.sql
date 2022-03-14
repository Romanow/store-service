-- V 1.0 Create users table
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    user_uid uuid         NOT NULL
);

CREATE UNIQUE INDEX idx_user_name ON users (name);
CREATE UNIQUE INDEX idx_user_user_uid ON users (user_uid);