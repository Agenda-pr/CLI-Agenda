package common.exception;

public class TaskIDNull extends RuntimeException {
    public TaskIDNull() {
        super("Task ID can't be null");
    }
}
