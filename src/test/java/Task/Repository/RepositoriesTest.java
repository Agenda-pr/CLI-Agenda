package Task.Repository;
import Task.Model.Priority;
import Task.Model.Task;
import Task.Service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoriesTest {
    private InRamRepository repository;
//    private OtherExampleRepository experimentalRepo;
    private TaskService provService;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void ini(){
        repository = new InRamRepository();
        task1 = new Task("Task 1", Priority.MEDIUM);
        task2 = new Task("Task 2", Priority.LOW);
    }

    @Test
    void saveReturnSameTask(){

        assertSame(task1, repository.save(task1));
    }

    @Test
    void saveAddTaskToList(){
        repository.save(task1);

        assertEquals(1, repository.showAllTasks().size());
        assertTrue(repository.showAllTasks().contains(task1));
    }

    //ToDo filteredTaskList Test

    @Test
    void findTaskReturnsTaskFromExistingId(){
        repository.save(task2);

        assertSame(task2, repository.findTask(task2.getId()));
    }

    @Test
    void findTaskReturnsNullUnknowId(){
        assertNull(repository.findTask(UUID.randomUUID()));
    }

//    @Test
//    public void shouldUseRamRepository(){
//        provService = new Repository(ramRepo);
//        Assertions.assertEquals("Task added on memory",
//                provService.addTask(task));
//    }

//    @Test
//    public void shouldUseOtherRepo(){
//        provService = new TaskService((Repository) experimentalRepo);
//        Assertions.assertEquals("Added on alternative repo",
//                provService.addTask(task));
//    }

//    @Test
//    public void shouldPrintAllThetask(){
//        provService = new TaskService(ramRepo);
//        String log = provService.createTask(task);
//        log += "\n" + provService.createTask(task2);
//
//        Assertions.assertEquals("Task added on memory\nTask added on memory", log);
//
//        String expected = "Tasca 1\n" + "Task 2";
//
//        Assertions.assertEquals(expected,provService.printAllTasks());
//
//    }

}
