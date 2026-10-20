package event.model;

import common.exception.InvalidEventException;
import java.time.LocalDate;

public record EventCelebDate(LocalDate date) {
    private static final LocalDate LIMIT_DATE = LocalDate.of(2000, 1, 1);

    public EventCelebDate {
        if (date == null) {
            throw new InvalidEventException("Event date cannot be null");
        }
        if (date.isBefore(LIMIT_DATE)) {
            throw new InvalidEventException("Date must not be before 01/01/2000");
        }
    }

    public static EventCelebDate of (LocalDate date) {
        return new EventCelebDate(date);
    }
}
