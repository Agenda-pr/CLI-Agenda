package event.dao;

import event.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.dao.TaskDAO;
import task.dao.TaskDAOAdapter;
import task.model.Priority;
import task.model.Task;
import task.model.TaskTitle;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOAdapterTest {
    private final EventDAO dao = new EventDAOAdapter();
    private final TaskDAO taskDao = new TaskDAOAdapter();
    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.create(
                EventTitle.of("Mom birthday"),
                EventCelebDate.of(LocalDate.now().plusDays(10)),
                EventRecurrenceType.YEARLY
        );
    }

    @AfterEach
    void cleanUp() {
        dao.deleteById(event.getId());
    }

    @Test
    void insertAndFindByIdReturnsSameData() {
        dao.insert(event);

        Event found = dao.findById(event.getId()).orElseThrow();

        assertEquals(event.getId(), found.getId());
        assertEquals(event.getTitle(), found.getTitle());
        assertEquals(event.getCelebDate(), found.getCelebDate());
        assertEquals(event.getEventRecurrenceType(), found.getEventRecurrenceType());
        assertEquals(event.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        assertTrue(dao.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void updateChangesTheFields() {
        dao.insert(event);

        event.setTitle(EventTitle.of("Mom birthday"));
        event.setCelebDate(EventCelebDate.of(LocalDate.now().plusDays(20)));
        event.setEventRecurrenceType(EventRecurrenceType.MONTHLY);

        assertTrue(dao.update(event));

        Event found = dao.findById(event.getId()).orElseThrow();
        assertEquals("Mom birthday", found.getTitle().title());
        assertEquals(EventRecurrenceType.MONTHLY, found.getEventRecurrenceType());
    }

    @Test
    void updateReturnsFalseWhenEventDoesNotExist() {
        assertFalse(dao.update(event));
    }

    @Test
    void findAllContainsInsertedEvent() {
        dao.insert(event);

        assertTrue(dao.findAll().stream()
                .anyMatch(e -> e.getId().equals(event.getId())));
    }

    @Test
    void deleteByIdRemovesEvent() {
        dao.insert(event);

        assertTrue(dao.deleteById(event.getId()));
        assertTrue(dao.findById(event.getId()).isEmpty());
        assertFalse(dao.deleteById(UUID.randomUUID()));
    }

    @Test
    void associatesAndRestoresTaskIdsCorrectly() {
        Task task = Task.create(TaskTitle.of("Buy present"), Priority.HIGH);
        taskDao.insert(task);

        try {
            event.addTaskId(EventTaskId.of(task.getId()));
            dao.insert(event);

            Event found = dao.findById(event.getId()).orElseThrow();
            assertEquals(1, found.getTaskIds().size());
            assertEquals(task.getId(), found.getTaskIds().get(0).id());
        } finally {
            taskDao.deleteById(task.getId());
        }
    }
}
