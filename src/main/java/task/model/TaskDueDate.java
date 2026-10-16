package task.model;

import common.exception.InvalidTaskException;
import java.time.LocalDate;

public record TaskDueDate(LocalDate date) {

    public static TaskDueDate create(LocalDate date) {
        if (date == null) {
            return null;
        }
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidTaskException("Due date cannot be in the past");
        }
        return new TaskDueDate(date);
    }

    public static TaskDueDate restore(LocalDate date) {
        if (date == null) {
            return null;
        }
        return new TaskDueDate(date);
    }
}
