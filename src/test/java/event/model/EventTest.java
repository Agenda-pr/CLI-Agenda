package event.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void createSetsCorrectDefaults() {
        EventCelebDate date = EventCelebDate.of(LocalDate.of(2026, 11, 20));
        Event event = Event.create(EventTitle.of("Dentista"), date);

        assertNotNull(event.getId());
        assertNotNull(event.getCreatedAt());
        assertEquals(EventTitle.of("Dentista"), event.getTitle());
        assertEquals(date, event.getCelebDate());
        assertEquals(EventRecurrenceType.NONE, event.getEventRecurrenceType());
    }

    @Test
    void createWithNullRecurrenceDefaultsToNone() {
        EventCelebDate date = EventCelebDate.of(LocalDate.of(2026, 11, 20));
        Event event = Event.create(EventTitle.of("Dentista"), date, null);

        assertEquals(EventRecurrenceType.NONE, event.getEventRecurrenceType());
    }

    @Test
    void createSavesCorrectValues() {
        EventCelebDate date = EventCelebDate.of(LocalDate.of(2026, 5, 15));
        Event event = Event.create(EventTitle.of("Cumpleaños"), date, EventRecurrenceType.YEARLY);

        assertEquals(EventTitle.of("Cumpleaños"), event.getTitle());
        assertEquals(date, event.getCelebDate());
        assertEquals(EventRecurrenceType.YEARLY, event.getEventRecurrenceType());
    }

    @Test
    void differentEventsHaveDifferentId() {
        EventCelebDate date = EventCelebDate.of(LocalDate.of(2026, 5, 15));
        Event event1 = Event.create(EventTitle.of("Evento 1"), date);
        Event event2 = Event.create(EventTitle.of("Evento 2"), date);

        assertNotEquals(event1.getId(), event2.getId());
    }

    @Test
    void restoreReconstitutesEventExactly() {
        UUID id = UUID.randomUUID();
        EventTitle title = EventTitle.of("Aniversario");
        EventCelebDate celebDate = EventCelebDate.of(LocalDate.of(2020, 6, 10));
        EventRecurrenceType recurrence = EventRecurrenceType.YEARLY;
        LocalDate createdAt = LocalDate.of(2020, 1, 1);

        Event event = Event.restore(id, title, celebDate, recurrence, createdAt);

        assertEquals(id, event.getId());
        assertEquals(title, event.getTitle());
        assertEquals(celebDate, event.getCelebDate());
        assertEquals(recurrence, event.getEventRecurrenceType());
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    void nextCelebDateKeepsSameDateWhenRecurrenceIsNone() {
        LocalDate initialDate = LocalDate.of(2026, 10, 21);
        Event event = Event.create(
                EventTitle.of("Cita única"),
                EventCelebDate.of(initialDate),
                EventRecurrenceType.NONE);

        event.nextCelebDate();

        assertEquals(initialDate, event.getCelebDate().date());
    }

    @Test
    void nextCelebDateAddsOneDayWhenRecurrenceIsDaily() {
        LocalDate initialDate = LocalDate.of(2026, 10, 21);
        Event event = Event.create(
                EventTitle.of("Gimnasio"),
                EventCelebDate.of(initialDate),
                EventRecurrenceType.DAILY);

        event.nextCelebDate();

        assertEquals(LocalDate.of(2026, 10, 22), event.getCelebDate().date());
    }

    @Test
    void nextCelebDateAddsOneWeekWhenRecurrenceIsWeekly() {
        LocalDate initialDate = LocalDate.of(2026, 10, 21);
        Event event = Event.create(
                EventTitle.of("Reunión semanal"),
                EventCelebDate.of(initialDate),
                EventRecurrenceType.WEEKLY);

        event.nextCelebDate();

        assertEquals(LocalDate.of(2026, 10, 28), event.getCelebDate().date());
    }

    @Test
    void nextCelebDateAddsOneMonthWhenRecurrenceIsMonthly() {
        LocalDate initialDate = LocalDate.of(2026, 10, 21);
        Event event = Event.create(
                EventTitle.of("Pagar alquiler"),
                EventCelebDate.of(initialDate),
                EventRecurrenceType.MONTHLY);

        event.nextCelebDate();

        assertEquals(LocalDate.of(2026, 11, 21), event.getCelebDate().date());
    }

    @Test
    void nextCelebDateAddsOneYearWhenRecurrenceIsYearly() {
        LocalDate initialDate = LocalDate.of(2026, 10, 21);
        Event event = Event.create(
                EventTitle.of("Cumpleaños"),
                EventCelebDate.of(initialDate),
                EventRecurrenceType.YEARLY);

        event.nextCelebDate();

        assertEquals(LocalDate.of(2027, 10, 21), event.getCelebDate().date());
    }
}
