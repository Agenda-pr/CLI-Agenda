package task.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Priority;
import task.model.Task;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {
    private InRamRepository repository;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void ini() {
        repository = new InRamRepository();
        task1 = Task.create("Task 1", Priority.MEDIUM);
        task2 = Task.create("Task 2", Priority.LOW);
    }

    @Test
    void saveReturnSameTask() {
        repository.save(task1);
        Task saved = repository.findTask(task1.getId());
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
        task1.setTitle("Task 1 Updated");

        repository.save(task1);

        assertEquals(1, repository.showAllTasks().size());
        assertEquals("Task 1 Updated", repository.findTask(task1.getId()).getTitle());
    }

    @Test
    void findTaskReturnsTaskFromExistingId() {
        repository.save(task2);

        Task found = repository.findTask(task2.getId());
        assertNotNull(found);
        assertEquals(task2.getId(), found.getId());
        assertEquals(task2.getTitle(), found.getTitle());
    }

    @Test
    void findTaskReturnsNullUnknowId() {
        assertNull(repository.findTask(UUID.randomUUID()));
    }
}
