CREATE TABLE IF NOT EXISTS patient
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    svnr INT,
    name VARCHAR(255),
    firstname VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    active BOOLEAN
)