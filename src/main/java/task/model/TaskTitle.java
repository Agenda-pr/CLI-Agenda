package task.model;

import common.exception.InvalidTaskException;

public record TaskTitle(String title) {

    public TaskTitle {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
        title = title.trim();
    }

    public static TaskTitle of(String title) {
        return new TaskTitle(title);
    }
}
