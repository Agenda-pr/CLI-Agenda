package task.model;

import common.exception.InvalidTaskException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TaskDueDateTest {

    @Test
    void createWithFutureDateReturnsTaskDueDate() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        TaskDueDate dueDate = TaskDueDate.create(futureDate);

        assertNotNull(dueDate);
        assertEquals(futureDate, dueDate.date());
    }

    @Test
    void createWithTodayDateReturnsTaskDueDate() {
        LocalDate today = LocalDate.now();
        TaskDueDate dueDate = TaskDueDate.create(today);

        assertNotNull(dueDate);
        assertEquals(today, dueDate.date());
    }

    @Test
    void createWithPastDateThrowsInvalidTaskException() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        InvalidTaskException exception = assertThrows(InvalidTaskException.class, () -> TaskDueDate.create(pastDate));
        assertEquals("Due date cannot be in the past", exception.getMessage());
    }

    @Test
    void createWithNullReturnsNull() {
        assertNull(TaskDueDate.create(null));
    }

    @Test
    void restoreWithNullReturnsNull() {
        assertNull(TaskDueDate.restore(null));
    }

    @Test
    void restoreWithPastDateAllowsOverdueTask() {
        LocalDate pastDate = LocalDate.now().minusDays(15);
        TaskDueDate dueDate = TaskDueDate.restore(pastDate);

        assertNotNull(dueDate);
        assertEquals(pastDate, dueDate.date());
    }

    @Test
    void restoreWithFutureDateReturnsTaskDueDate() {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        TaskDueDate dueDate = TaskDueDate.restore(futureDate);

        assertNotNull(dueDate);
        assertEquals(futureDate, dueDate.date());
    }

    @Test
    void twoInstancesWithSameDateAreEqual() {
        LocalDate date = LocalDate.of(2026, 12, 31);
        TaskDueDate dueDate1 = TaskDueDate.create(date);
        TaskDueDate dueDate2 = TaskDueDate.create(date);

        assertEquals(dueDate1, dueDate2);
        assertEquals(dueDate1.hashCode(), dueDate2.hashCode());
    }
}
