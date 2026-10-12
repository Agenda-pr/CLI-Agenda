package Task.Repository;

import Task.Model.Task;

import java.util.List;
import java.util.UUID;

public class OtherExampleRepository implements Repository{
    @Override
    public String addTask(Task task) {
        return "Added on alternative repo";
    }

    @Override
    public List<Task> showAllTasks() {
        return List.of();
    }

    @Override
    public List<Task> filteredTaskList(String query) {
        return List.of();
    }

    @Override
    public Task findTask(UUID id) {
        return null;
    }
}
