package task.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Priority;
import task.model.Task;

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
        assertSame(task1, repository.save(task1));
    }

    @Test
    void saveAddTaskToList() {
        repository.save(task1);

        assertEquals(1, repository.showAllTasks().size());
        assertTrue(repository.showAllTasks().contains(task1));
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

        assertSame(task2, repository.findTask(task2.getId()));
    }

    @Test
    void findTaskReturnsNullUnknowId() {
        assertNull(repository.findTask(UUID.randomUUID()));
    }
}
