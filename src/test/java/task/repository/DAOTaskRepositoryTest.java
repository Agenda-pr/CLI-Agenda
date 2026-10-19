package task.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.dao.TaskDAOAdapter;
import task.model.Priority;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTaskRepositoryTest {
    private final TaskRepository repository = new DAOTaskRepository(new TaskDAOAdapter());
    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.create(TaskTitle.of("Repository test task"), Priority.HIGH,
                TaskDueDate.create(LocalDate.now().plusDays(3)));
    }

    @AfterEach
    void cleanUp() {
        repository.deleteTaskByID(task.getId());
    }

    @Test
    void saveNewTaskInsertsIt() {
        repository.save(task);

        Task found = repository.findTaskByID(task.getId());

        assertNotNull(found);
        assertEquals(task.getId(), found.getId());
        assertEquals(task.getTitle(), found.getTitle());
        assertEquals(task.getPriority(), found.getPriority());
        assertEquals(task.getDueDate(), found.getDueDate());
        assertFalse(found.isCompleted());
    }

    @Test
    void saveExistingTaskUpdatesItWithoutDuplicating() {
        repository.save(task);

        task.setTitle(TaskTitle.of("Changed title"));
        task.setPriority(Priority.LOW);
        task.setCompleted(true);
        repository.save(task);

        Task found = repository.findTaskByID(task.getId());
        assertEquals("Changed title", found.getTitle().title());
        assertEquals(Priority.LOW, found.getPriority());
        assertTrue(found.isCompleted());

        long copies = repository.showAllTasks().stream()
                .filter(t -> t.getId().equals(task.getId()))
                .count();
        assertEquals(1, copies);
    }

    @Test
    void findTaskByIDReturnsNullWhenNotFound() {
        assertNull(repository.findTaskByID(UUID.randomUUID()));
    }

    @Test
    void showAllTasksContainsSavedTask() {
        repository.save(task);

        assertTrue(repository.showAllTasks().stream()
                .anyMatch(t -> t.getId().equals(task.getId())));
    }

    @Test
    void deleteTaskByIDRemovesTask() {
        repository.save(task);

        assertTrue(repository.deleteTaskByID(task.getId()));
        assertNull(repository.findTaskByID(task.getId()));
    }

    @Test
    void deleteTaskByIDReturnsFalseWhenNotFound() {
        assertFalse(repository.deleteTaskByID(UUID.randomUUID()));
    }
}
