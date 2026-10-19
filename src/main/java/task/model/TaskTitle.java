package task.model;

import common.exception.InvalidTaskException;

public record TaskTitle (String title) {
    public static TaskTitle of(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
        return new TaskTitle(title.trim());
    }

}
