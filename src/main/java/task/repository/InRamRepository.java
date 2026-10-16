package task.repository;

import task.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InRamRepository implements Repository {
    private final List<Task> taskList = new ArrayList<>();

    @Override
    public void save(Task task) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId().equals(task.getId())) {
                taskList.set(i, task);
                return;
            }
        }
        taskList.add(task);
    }


    @Override
    public List<Task> showAllTasks() {
        return taskList.stream()
                .map(this::restoreFrom)
                .toList();
    }

    // DUDANDO
    @Override
    public List<Task> filteredTaskList(String query) {
        return List.of();
    }

    @Override
    public Task findTask(UUID ID) {
        return this.taskList.stream()
                .filter(task -> task.getId().equals(ID))
                .findFirst()
                .map(this::restoreFrom)
                .orElse(null);
    }

    private Task restoreFrom(Task task) {
        return Task.restore(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.isCompleted(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

}
