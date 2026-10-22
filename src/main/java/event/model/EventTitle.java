package event.model;

import common.exception.InvalidEventException;

public record EventTitle(String title) {
    public EventTitle {
        if (title == null || title.isBlank()) {
            throw new InvalidEventException("Event title cannot be empty or blank");
        }
        title = title.trim();
    }

    public static EventTitle of(String title) {
        return new EventTitle(title);
    }
}
