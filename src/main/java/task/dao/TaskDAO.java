package task.dao;

import task.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskDAO {
    void insert(Task task);
    boolean update(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findAll();
    boolean deleteById(UUID id);
}
