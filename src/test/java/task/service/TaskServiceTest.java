package task.service;

import task.model.Priority;
import task.model.Task;
import task.repository.InRamRepository;
import common.exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private InRamRepository repository;
    private TaskService service;

    @BeforeEach
    void ini (){
        repository= new InRamRepository();
        service = new TaskService(repository);
    }

    @Test
    void createTaskSavesInRepository () {
        Task createdTask = service.createTask("Task 1", Priority.LOW, null);

        assertEquals(1, repository.showAllTasks().size());
        assertEquals("Task 1", createdTask.getTitle());
    }

    @Test
    void createTaskTrimTitle(){
        Task task1 = service.createTask("    Task1", Priority.HIGH,null);

        assertEquals("Task1", task1.getTitle());
    }

    @ParameterizedTest
    @NullAndEmptySource //Prueba Null and Empty
    @ValueSource (strings = {"   "})
    void createTaskThrowsException(String title) {

        assertThrows(InvalidTaskException.class,
                () -> service.createTask(title, Priority.LOW, null));

        assertTrue(repository.showAllTasks().isEmpty());
    }

    @Nested
    class ListTask {
        private Task task1;
        private Task task2;
        private Task task3;

        @BeforeEach
        void set(){
            task1 = service.createTask("Task 1", Priority.LOW, LocalDate.of(2026, 12, 10));
            task2 = service.createTask("Task 2", Priority.MEDIUM, LocalDate.now().plusDays(3));
            task3 = service.createTask("Task 3", Priority.HIGH, null);
        }

        @Test
        void listTasksSortByCreationDate(){
            assertEquals(List.of(task1,task2,task3), service.listTasksByCreation());
        }

        @Test
        void listTasksReturnsAll(){
            List<Task> listResult = service.listTasksByCreation();

            assertEquals(3, listResult.size());
            assertTrue(listResult.containsAll(List.of(task1,task2,task3)));
        }

    }





    //   @Test
//    void printAllTasks_joinsTitlesByLine() {
//        service.createTask("Task 1", Priority.MEDIUM, null);
//        service.createTask("Task 2", Priority.LOW, null);
//
//        assertEquals("Task 1\nTask 2", service.listTasksForCreation());
//    }
//
//    @Test
//    void printAllTasks_empty_returnsEmptyString() {
//        assertEquals("", service.listTasksForCreation());
//    }


//        public Task createTask(String title, Priority priority, LocalDate dueDate){
//            if(title == null || title.isBlank()){
//                throw new InvalidTaskException("Task title cannot be empty or blank");
//            }
//            Task task = new Task(title.trim(), priority, dueDate);
//            return repository.save(task);
//        }
//
//        public String printAllTasks(){
//            return this.repository.showAllTasks().stream()
//                    .map(Task::getTitle)
//                    .collect(Collectors.joining("\n"));
//        }


}
