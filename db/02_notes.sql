CREATE TABLE IF NOT EXIST notes (
    id         CHAR(36)     NOT NULL,
    task_id    CHAR(36)  NOT NULL,
    content    VARCHAR(200) NOT NULL,
    created_at DATETIME     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);