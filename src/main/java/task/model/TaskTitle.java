package task.model;

import common.exception.InvalidTaskException;
import java.util.Objects;

public final class TaskTitle {
    private final String title;

    private TaskTitle(String title) {
        this.title = title;
    }

    public static TaskTitle of(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
        return new TaskTitle(title.trim());
    }

    public String title() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskTitle taskTitle))
            return false;
        return Objects.equals(title, taskTitle.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

}
