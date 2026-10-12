package Task.Repository;

import Task.Model.Task;

import java.util.List;
import java.util.UUID;

public interface Repository {
    String addTask(Task task);
    List<Task> showAllTasks();
    List<Task> filteredTaskList (String query); // No se si va
    Task findTask(UUID id);
}
