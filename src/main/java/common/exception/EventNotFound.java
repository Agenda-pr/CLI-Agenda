package common.exception;

public class EventNotFound extends RuntimeException {
    public EventNotFound() {
        super("Event not found");
    }

    public EventNotFound(String message) {
        super(message);
    }
}
