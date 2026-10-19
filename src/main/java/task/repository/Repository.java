package task.repository;

import task.model.Task;

import java.util.List;
import java.util.UUID;

public interface Repository {
    void save(Task task);
    List<Task> showAllTasks();
    List<Task> filteredTaskList (String query); // No se si va
    Task findTaskByID(UUID id); //
    boolean deleteTaskByID(UUID id);
    List<Task> listCompletedTask();
}
