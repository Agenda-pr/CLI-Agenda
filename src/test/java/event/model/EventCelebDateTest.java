package event.model;

import common.exception.InvalidEventException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EventCelebDateTest {

    @Test
    void ofWithValidDateReturnsEventCelebDate() {
        LocalDate date = LocalDate.of(2026, 5, 15);
        EventCelebDate celebDate = EventCelebDate.of(date);

        assertNotNull(celebDate);
        assertEquals(date, celebDate.date());
    }

    @Test
    void ofWithLimitDateYear2000ReturnsEventCelebDate() {
        LocalDate limitDate = LocalDate.of(2000, 1, 1);
        EventCelebDate celebDate = EventCelebDate.of(limitDate);

        assertNotNull(celebDate);
        assertEquals(limitDate, celebDate.date());
    }

    @Test
    void ofWithDateBeforeYear2000ThrowsInvalidEventException() {
        LocalDate invalidDate = LocalDate.of(1999, 12, 31);

        InvalidEventException exception = assertThrows(
                InvalidEventException.class,
                () -> EventCelebDate.of(invalidDate));
        assertEquals("Date must not be before 01/01/2000", exception.getMessage());
    }

    @Test
    void ofWithNullThrowsInvalidEventException() {
        InvalidEventException exception = assertThrows(
                InvalidEventException.class,
                () -> EventCelebDate.of(null));
        assertEquals("Event date cannot be null", exception.getMessage());
    }

    @Test
    void twoInstancesWithSameDateAreEqual() {
        LocalDate date = LocalDate.of(2026, 10, 21);
        EventCelebDate date1 = EventCelebDate.of(date);
        EventCelebDate date2 = EventCelebDate.of(date);

        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }
}
