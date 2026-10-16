package task.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void constructorSetCorrectDefaults() {
        Task task = Task.create(new TaskTitle("Go to supermarket"), null);

        assertNotNull(task.getId());
        assertNotNull(task.getCreatedAt());
        assertNull(task.getDueDate());
        assertFalse(task.isCompleted());
        assertEquals(Priority.MEDIUM, task.getPriority());
    }

    @Test
    void contructorSaveCorrectValues() {
        TaskDueDate date = TaskDueDate.create(LocalDate.of(2026, 11, 20));
        Task task = Task.create(new TaskTitle("Finish this issue"), Priority.HIGH, date);

        assertEquals(new TaskTitle("Finish this issue"), task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(date, task.getDueDate());
    }

    @Test
    void differentTasksHaveDifferentId() {
        Task task1 = Task.create(new TaskTitle("Task1"), Priority.LOW);
        Task task2 = Task.create(new TaskTitle("Task2"), Priority.LOW);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void restoreReconstitutesTaskExactly() {
        UUID id = UUID.randomUUID();
        TaskTitle title = new TaskTitle("Historic Task");
        Priority priority = Priority.HIGH;
        boolean completed = true;
        TaskDueDate dueDate = TaskDueDate.restore(LocalDate.of(2025, 1, 15));
        LocalDateTime createdAt = LocalDateTime.of(2024, 12, 1, 10, 0);

        Task task = Task.restore(id, title, priority, completed, dueDate, createdAt);

        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(priority, task.getPriority());
        assertTrue(task.isCompleted());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(createdAt, task.getCreatedAt());
    }
}
