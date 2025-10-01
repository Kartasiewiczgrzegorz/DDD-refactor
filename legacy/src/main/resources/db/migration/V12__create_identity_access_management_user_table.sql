CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       invalid_login_counter INT NOT NULL,
                       surname VARCHAR(255),
                       verification VARCHAR(50) NOT NULL,
                       blocked VARCHAR(50) NOT NULL,
                       CONSTRAINT uq_users_email UNIQUE (email)
);