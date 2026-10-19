package task.repository;

import task.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository {
    void save(Task task);
    List<Task> showAllTasks();
    Task findTaskByID(UUID id);
    boolean deleteTaskByID(UUID id);
}
