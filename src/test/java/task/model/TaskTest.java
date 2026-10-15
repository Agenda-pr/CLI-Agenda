package task.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void constructorSetCorrectDefaults(){
        Task task = Task.create ("Go to supermarket", null);

        assertNotNull(task.getId());
        assertNotNull(task.getCreatedAt());
        assertNull(task.getDueDate());
        assertFalse(task.isCompleted());
        assertEquals(Priority.MEDIUM, task.getPriority());
    }

   @Test
    void contructorSaveCorrectValues(){
       LocalDate date = LocalDate.of(2026,11,20);
       Task task = Task.create("Finish this issue", Priority.HIGH, date);

       assertEquals("Finish this issue", task.getTitle());
       assertEquals(Priority.HIGH, task.getPriority());
       assertEquals(date,task.getDueDate());
   }

   @Test
    void differentTasksHaveDifferentId() {
        Task task1= Task.create("Task1", Priority.LOW);
        Task task2 = Task.create("Task2", Priority.LOW);

        assertNotEquals(task1.getId(), task2.getId());
   }
}
