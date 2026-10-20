package task.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Priority;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskDAOAdapterTest {
    private final TaskDAO dao = new TaskDAOAdapter();
    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.create(TaskTitle.of("DAO test task"), Priority.HIGH,
                TaskDueDate.create(LocalDate.now().plusDays(3)));
    }

    @AfterEach
    void cleanUp() {
        dao.deleteById(task.getId());
    }

    @Test
    void insertAndFindByIdReturnsSameData() {
        dao.insert(task);

        Task found = dao.findById(task.getId()).orElseThrow();

        assertEquals(task.getId(), found.getId());
        assertEquals(task.getTitle(), found.getTitle());
        assertEquals(task.getPriority(), found.getPriority());
        assertEquals(task.isCompleted(), found.isCompleted());
        assertEquals(task.getDueDate(), found.getDueDate());
    }

    @Test
    void insertWithoutDueDateStoresNull() {
        Task noDate = Task.create(TaskTitle.of("No date"), null);
        dao.insert(noDate);
        try {
            Task found = dao.findById(noDate.getId()).orElseThrow();
            assertNull(found.getDueDate());
            assertEquals(Priority.MEDIUM, found.getPriority());
        } finally {
            dao.deleteById(noDate.getId());
        }
    }

    @Test
    void updateChangesTheFields() {
        dao.insert(task);
        task.setTitle(TaskTitle.of("Changed title"));
        task.setPriority(Priority.LOW);
        task.setCompleted(true);

        assertTrue(dao.update(task));

        Task found = dao.findById(task.getId()).orElseThrow();
        assertEquals("Changed title", found.getTitle().title());
        assertEquals(Priority.LOW, found.getPriority());
        assertTrue(found.isCompleted());
    }

    @Test
    void updateReturnsFalseWhenTaskDoesNotExist() {
        assertFalse(dao.update(task));
    }

    @Test
    void findAllContainsInsertedTask() {
        dao.insert(task);
        assertTrue(dao.findAll().stream().anyMatch(t -> t.getId().equals(task.getId())));
    }

    @Test
    void deleteByIdRemovesTask() {
        dao.insert(task);

        assertTrue(dao.deleteById(task.getId()));
        assertTrue(dao.findById(task.getId()).isEmpty());
        assertFalse(dao.deleteById(UUID.randomUUID()));
    }
}
