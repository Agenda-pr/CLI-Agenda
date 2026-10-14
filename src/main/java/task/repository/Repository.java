package task.repository;

import task.dto.TaskDto;
import task.model.Task;

import java.util.List;
import java.util.UUID;

public interface Repository {
    Task save(Task task);
    List<Task> showAllTasks();
    List<Task> filteredTaskList (String query); // No se si va
    Task findTask(UUID id); //    Optional<Task> findById;
}
