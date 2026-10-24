CREATE TABLE IF NOT EXISTS tasks (
    id         CHAR(36)     NOT NULL,
    event_id   CHAR(36)     NULL,
    title      VARCHAR(255) NOT NULL,
    priority   ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    completed  BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date   DATE         NULL,
    created_at DATETIME     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
    );