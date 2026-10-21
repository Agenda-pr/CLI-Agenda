CREATE TABLE IF NOT EXISTS events (
    id         CHAR(36)                       NOT NULL,
    id_task     CHAR(36)                      NULL,
    title      VARCHAR(255)                   NOT NULL,
    date       DATE                           NOT NULL,
    recurrence  ENUM (norecu, anualy, custom) NOT NULL,
    created_at DATETIME                       NOT NULL,
    PRIMARY KEY (id)
);