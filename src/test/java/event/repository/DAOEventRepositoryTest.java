package event.repository;

import event.dao.EventDAOAdapter;
import event.model.Event;
import event.model.EventCelebDate;
import event.model.EventRecurrenceType;
import event.model.EventTitle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DAOEventRepositoryTest {
    private final EventRepository repository = new DAOEventRepository(new EventDAOAdapter());
    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.create(
                EventTitle.of("Team meeting"),
                EventCelebDate.of(LocalDate.now().plusDays(3)),
                EventRecurrenceType.WEEKLY
        );
    }

    @AfterEach
    void cleanUp() {
        repository.deleteEventById(event.getId());
    }

    @Test
    void saveNewEventInsertsIt() {
        repository.save(event);

        Event found = repository.findEventById(event.getId());

        assertNotNull(found);
        assertEquals(event.getId(), found.getId());
        assertEquals(event.getTitle(), found.getTitle());
        assertEquals(event.getCelebDate(), found.getCelebDate());
        assertEquals(event.getEventRecurrenceType(), found.getEventRecurrenceType());
    }

    @Test
    void saveExistingEventUpdatesItWithoutDuplicating() {
        repository.save(event);

        event.setTitle(EventTitle.of("Change meeting"));
        event.setEventRecurrenceType(EventRecurrenceType.NONE);
        repository.save(event);

        Event found = repository.findEventById(event.getId());
        assertEquals("Change meeting", found.getTitle().title());
        assertEquals(EventRecurrenceType.NONE, found.getEventRecurrenceType());

        long totalCopies = repository.showAllEvents().stream()
                .filter(e -> e.getId().equals(event.getId()))
                .count();
        assertEquals(1, totalCopies);
    }

    @Test
    void findEventByIdReturnsNullWhenNotFound() {
        assertNull(repository.findEventById(UUID.randomUUID()));
    }

    @Test
    void showAllEventsContainsSavedEvent() {
        repository.save(event);

        assertTrue(repository.showAllEvents().stream()
                .anyMatch(e -> e.getId().equals(event.getId())));
    }

    @Test
    void deleteEventByIdRemovesEvent() {
        repository.save(event);

        assertTrue(repository.deleteEventById(event.getId()));
        assertNull(repository.findEventById(event.getId()));
    }

    @Test
    void deleteEventByIdReturnsFalseWhenNotFound() {
        assertFalse(repository.deleteEventById(UUID.randomUUID()));
    }
}
