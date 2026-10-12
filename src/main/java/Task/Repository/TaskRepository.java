package Task.Repository;

import Task.Model.Task2;

import java.util.List;
import java.util.UUID;

public interface TaskRepository {
    Task2 save(Task2 task);
//    List<Task2> findAll();
//    boolean deleteById(UUID id);
}
