package event.repository;

import event.model.Event;
import event.model.EventCelebDate;
import event.model.EventRecurrenceType;
import event.model.EventTaskId;
import event.model.EventTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InRamEventRepositoryTest {
    private InRamEventRepository repository;
    private Event event1;
    private Event event2;

    @BeforeEach
    public void ini() {
        repository = new InRamEventRepository();
        event1 = Event.create(
                EventTitle.of("Event 1"),
                EventCelebDate.of(LocalDate.of(2026, 5, 15)),
                EventRecurrenceType.YEARLY);
        event2 = Event.create(
                EventTitle.of("Event 2"),
                EventCelebDate.of(LocalDate.of(2026, 8, 20)));
    }

    @Test
    void saveReturnsSameEvent() {
        repository.save(event1);

        Event saved = repository.findEventById(event1.getId());
        assertNotNull(saved);
        assertEquals(event1.getId(), saved.getId());
        assertEquals(event1.getTitle(), saved.getTitle());
        assertEquals(event1.getCelebDate(), saved.getCelebDate());
        assertEquals(event1.getEventRecurrenceType(), saved.getEventRecurrenceType());
    }

    @Test
    void saveAddsEventToList() {
        repository.save(event1);

        List<Event> events = repository.showAllEvents();
        assertEquals(1, events.size());
        assertEquals(event1.getId(), events.get(0).getId());
    }

    @Test
    void saveUpdatesExistingEventWithoutDuplicating() {
        repository.save(event1);
        event1.setTitle(EventTitle.of("Event 1 Updated"));

        repository.save(event1);

        assertEquals(1, repository.showAllEvents().size());
        assertEquals(EventTitle.of("Event 1 Updated"), repository.findEventById(event1.getId()).getTitle());
    }

    @Test
    void findEventByIdReturnsEventFromExistingId() {
        repository.save(event2);

        Event found = repository.findEventById(event2.getId());
        assertNotNull(found);
        assertEquals(event2.getId(), found.getId());
        assertEquals(event2.getTitle(), found.getTitle());
    }

    @Test
    void findEventByIdReturnsNullUnknownId() {
        assertNull(repository.findEventById(UUID.randomUUID()));
    }

    @Test
    void deleteEventByIdReturnsTrueExistingId() {
        repository.save(event1);

        boolean result = repository.deleteEventById(event1.getId());

        assertTrue(result);
        assertNull(repository.findEventById(event1.getId()));
        assertTrue(repository.showAllEvents().isEmpty());
    }

    @Test
    void deleteEventByIdReturnsFalseUnknownId() {
        assertFalse(repository.deleteEventById(UUID.randomUUID()));
    }

    @Test
    void removeTaskFromAllEventsRemovesTaskIdFromEveryEvent() {
        UUID targetTaskId = UUID.randomUUID();
        UUID otherTaskId = UUID.randomUUID();
        event1.addTaskId(EventTaskId.of(targetTaskId));
        event1.addTaskId(EventTaskId.of(otherTaskId));
        event2.addTaskId(EventTaskId.of(targetTaskId));
        repository.save(event1);
        repository.save(event2);

        repository.removeTaskFromAllEvents(targetTaskId);

        assertEquals(List.of(EventTaskId.of(otherTaskId)), repository.findEventById(event1.getId()).getTaskIds());
        assertTrue(repository.findEventById(event2.getId()).getTaskIds().isEmpty());
    }
}
