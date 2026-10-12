package Task.Repository;

import Task.Model.Task;

import java.util.List;
import java.util.UUID;

public interface Repository {
    Task save(Task task);
    String addTask(Task task);
    List<Task> showAllTasks();
    List<Task> filteredTaskList (String query); // No se si va
    Task findTask(UUID id);
//    List<Task2> findAll();
//    boolean deleteById(UUID id);
}
