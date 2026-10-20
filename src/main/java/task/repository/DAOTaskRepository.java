package task.repository;

import task.dao.TaskDAO;
import task.model.Task;

import java.util.List;
import java.util.UUID;

public class DAOTaskRepository implements TaskRepository{
    private final TaskDAO dao;

    public DAOTaskRepository(TaskDAO dao) {
        this.dao = dao;
    }

    @Override
    public void save(Task task) {
        if (!dao.update(task)) {
            dao.insert(task);
        }
    }

    @Override
    public List<Task> showAllTasks() {
        return dao.findAll();
    }

    @Override
    public Task findTaskByID(UUID id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public boolean deleteTaskByID(UUID id) {
        return dao.deleteById(id);
    }
}
