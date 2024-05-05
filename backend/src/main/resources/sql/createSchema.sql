CREATE TABLE IF NOT EXISTS patient
(
    uid       BIGINT AUTO_INCREMENT PRIMARY KEY,
    svnr      INT,
    name      VARCHAR(255),
    firstname VARCHAR(255),
    username  VARCHAR(255),
    password  VARCHAR(255),
    email     VARCHAR(255),
    active    BOOLEAN
);

CREATE TABLE IF NOT EXISTS outpatient_department
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    capacity    INT
);

CREATE TABLE IF NOT EXISTS opening_hours
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    outpatient_department_id BIGINT,
    monday                   VARCHAR(255),
    tuesday                  VARCHAR(255),
    wednesday                VARCHAR(255),
    thursday                 VARCHAR(255),
    friday                   VARCHAR(255),
    saturday                 VARCHAR(255),
    sunday                   VARCHAR(255),
    FOREIGN KEY (outpatient_department_id) REFERENCES outpatient_department (id)
);