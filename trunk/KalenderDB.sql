CREATE TABLE employee (
    username VARCHAR(16) NOT NULL PRIMARY KEY,
    name VARCHAR(30),
    password VARCHAR(16) NOT NULL,
);

CREATE TABLE alert (
    read BOOLEAN,
    time DATE,
    message VARCHAR(255),
    username VARCHAR(16) NOT NULL,
    activityID INT NOT NULL,
    PRIMARY KEY (username, activityID),
    FOREIGN KEY (username) REFERENCES employee(username)
        ON UPDATE CASCADE,
    FOREIGN KEY (activityID) REFERENCES activity(activityID)
        ON UPDATE CASCADE
);

CREATE TABLE participant (
    status INT,
    username VARCHAR(16) NOT NULL,
    activityID INT NOT NULL,
    PRIMARY KEY (username, activityID),
    FOREIGN KEY (username) REFERENCES employee(username)
        ON UPDATE CASCADE,
    FOREIGN KEY (activityID) REFRENCES activity(activityID)
        ON UPDATE CASCADE
);

CREATE TABLE activity (
    activityID INT NOT NULL PRIMARY KEY,
    starttime DATE,
    endtime DATE,
    description VARCHAR(255),
    cancelled BOOLEAN,
    username VARCHAR(16) NOT NULL,
    roomID INT,
    FOREIGN KEY (username) REFERENCES employee(username)
        ON UPDATE CASCADE,
    FOREIGN KEY (roomID) REFERENCES room(roomID)
        ON UPDATE CASCADE
);

CREATE TABLE room (
    roomID INT NOT NULL PRIMARY KEY,
    name VARCHAR(30)
);