package task.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Priority;
import task.model.Task;
import task.model.TaskTitle;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InRamTaskRepositoryTest {
    private InRamTaskRepository repository;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void ini() {
        repository = new InRamTaskRepository();
        task1 = Task.create(TaskTitle.of("Task 1"), Priority.MEDIUM);
        task2 = Task.create(TaskTitle.of("Task 2"), Priority.LOW);
    }

    @Test
    void saveReturnSameTask() {
        repository.save(task1);
        Task saved = repository.findTaskByID(task1.getId());
        assertEquals(task1.getId(), saved.getId());
        assertEquals(task1.getTitle(), saved.getTitle());
    }

    @Test
    void saveAddTaskToList() {
        repository.save(task1);

        List<Task> tasks = repository.showAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1.getId(), tasks.get(0).getId());
    }

    @Test
    void saveUpdatesExistingTaskWithoutDuplicating() {
        repository.save(task1);
        task1.setTitle(TaskTitle.of("Task 1 Updated"));

        repository.save(task1);

        assertEquals(1, repository.showAllTasks().size());
        assertEquals(TaskTitle.of("Task 1 Updated"), repository.findTaskByID(task1.getId()).getTitle());
    }

    @Test
    void findTaskReturnsTaskByIDFromExistingId() {
        repository.save(task2);

        Task found = repository.findTaskByID(task2.getId());
        assertNotNull(found);
        assertEquals(task2.getId(), found.getId());
        assertEquals(task2.getTitle(), found.getTitle());
    }

    @Test
    void findTaskByIDReturnsNullUnknowId() {
        assertNull(repository.findTaskByID(UUID.randomUUID()));
    }

    @Test
    void deleteTaskByIDReturnsTrueExistingId() {
        repository.save(task1);

        boolean result = repository.deleteTaskByID(task1.getId());

        assertTrue(result);
        assertNull(repository.findTaskByID(task1.getId()));
        assertTrue(repository.showAllTasks().isEmpty());
    }

    @Test
    void deleteTaskByIDReturnsFalseUnknowId() {
        assertFalse(repository.deleteTaskByID(UUID.randomUUID()));
    }
}
