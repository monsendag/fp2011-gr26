CREATE TABLE employee (
    username VARCHAR(16) NOT NULL PRIMARY KEY,
    name VARCHAR(30),
    password VARCHAR(32) NOT NULL,
);

CREATE TABLE alert (
    read BOOLEAN,
    time TIMESTAMP,
    message VARCHAR(255),
    username VARCHAR(16) NOT NULL,
    activityID INT NOT NULL,
    PRIMARY KEY (username, activityID),
    FOREIGN KEY (username) REFERENCES employee(username),
    FOREIGN KEY (activityID) REFERENCES activity(activityID)
);

CREATE TABLE participant (
    status INT,
    username VARCHAR(16) NOT NULL,
    activityID INT NOT NULL,
    PRIMARY KEY (username, activityID),
    FOREIGN KEY (username) REFERENCES employee(username),
    FOREIGN KEY (activityID) REFRENCES activity(activityID)
);

CREATE TABLE activity (
    activityID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    starttime TIMESTAMP,
    endtime TIMESTAMP,
    description VARCHAR(255),
    cancelled BOOLEAN,
    username VARCHAR(16) NOT NULL,
    roomID INT,
    location VARCHAR(30),
    ismeeting BOOLEAN,
    FOREIGN KEY (username) REFERENCES employee(username),
    FOREIGN KEY (roomID) REFERENCES room(roomID)
);

CREATE TABLE room (
    roomID INT NOT NULL PRIMARY KEY,
    name VARCHAR(30),
    capacity INT
);