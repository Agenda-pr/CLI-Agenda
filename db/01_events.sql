CREATE TABLE IF NOT EXISTS events (
    id         CHAR(36)                                              NOT NULL,
    title      VARCHAR(255)                                          NOT NULL,
    date       DATE                                                  NOT NULL,
    recurrence  ENUM ('NONE', 'DAILY','WEEKLY','MONTHLY', 'YEARLY')  NOT NULL DEFAULT 'NONE',
    created_at DATETIME                                              NOT NULL,
    PRIMARY KEY (id)
);