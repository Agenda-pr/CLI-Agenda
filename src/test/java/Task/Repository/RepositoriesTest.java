package Task.Repository;
import Task.Model.Priority;
import Task.Model.Task;
import Task.Service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositoriesTest {
    private InRamRepository ramRepo;
    private OtherExampleRepository experimentalRepo;
    private TaskService provService;
    private Task task;
    private Task task2;

    @BeforeEach
    public void ini(){
        ramRepo = new InRamRepository();
        experimentalRepo = new OtherExampleRepository();
        task = new Task("Tasca 1", Priority.MEDIUM);
        task2 = new Task("Task 2", Priority.LOW);
    }

//    @Test
//    public void shouldUseRamRepository(){
//        provService = new Repository(ramRepo);
//        Assertions.assertEquals("Task added on memory",
//                provService.addTask(task));
//    }

    @Test
    public void shouldUseOtherRepo(){
        provService = new TaskService((Repository) experimentalRepo);
        Assertions.assertEquals("Added on alternative repo",
                provService.addTask(task));
    }

    @Test
    public void shouldPrintAllThetask(){
        provService = new TaskService(ramRepo);
        String log = provService.addTask(task);
        log += "\n" + provService.addTask(task2);

        Assertions.assertEquals("Task added on memory\nTask added on memory", log);

        String expected = "Tasca 1\n" + "Task 2";

        Assertions.assertEquals(expected,provService.printAllTasks());

    }

}
