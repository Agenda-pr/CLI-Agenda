package Task.Repository;
import Task.Model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InRamRepository implements Repository{
    private final List<Task> taskList = new ArrayList<>();

    @Override
    public String addTask(Task task) {
        taskList.add(task);
        return "Task added on memory";
    }

    @Override
    public List<Task> showAllTasks() {
        return this.taskList;
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
                .orElse(null);
    }

    // Debería haber un método para editar una task o eso va en service? porque si vas a editarla tendré que encontrarla y guardarla en la mismo posición, no?
}
