-- section users
CREATE TABLE IF NOT EXISTS credentials
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(255),
    isActive Boolean,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS doctor
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_ref BIGINT,
    FOREIGN KEY (uid) REFERENCES credentials(uid)
);

CREATE TABLE IF NOT EXISTS admin
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_ref BIGINT,
    FOREIGN KEY (uid) REFERENCES credentials(uid)
);

CREATE TABLE IF NOT EXISTS secretary
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_ref BIGINT,
    FOREIGN KEY (uid) REFERENCES credentials(uid)
);

CREATE TABLE IF NOT EXISTS patient
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    svnr INT,
    password VARCHAR(255),
    email VARCHAR(255)
);

-- section static data
CREATE TABLE IF NOT EXISTS allergies
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS medicine
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ambulance
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    capacity BIGINT
);

-- table for each day
CREATE TABLE IF NOT EXISTS opening_hours
(
    ambulance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weekday VARCHAR(10),
    opening_time TIME,
    closing_time TIME,

    FOREIGN KEY (ambulance_id) REFERENCES ambulance(uid)
);

CREATE TABLE IF NOT EXISTS station
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    capacity BIGINT
);

CREATE TABLE IF NOT EXISTS residence
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_uid BIGINT,
    startDate DATETIME,
    endDate DATETIME,

    FOREIGN KEY(patient_uid) REFERENCES Patient(uid)
);

-- section dynamic data
CREATE TABLE IF NOT EXISTS appointment
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    startDate DATETIME,
    endDate DATETIME,
    notes VARCHAR(2048), -- TODO: discuss max note length
    ambulance_id BIGINT,

    FOREIGN KEY (ambulance_id) REFERENCES ambulance(uid)
);

CREATE TABLE IF NOT EXISTS treatment
(
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    startDate DATETIME,
    endDate DATETIME,
    doctor BIGINT,
    notes VARCHAR(2048),

    FOREIGN KEY (doctor) REFERENCES doctor(uid)
);

CREATE TABLE IF NOT EXISTS messages
(
    sender BIGINT,
    treatment BIGINT,
    message VARCHAR(2048),
    date DATETIME,

    FOREIGN KEY(treatment) REFERENCES Treatment(uid),
    FOREIGN KEY(sender) REFERENCES credentials(uid)
);