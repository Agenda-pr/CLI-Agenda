package Task.Service;

import Task.Model.Priority;
import Task.Model.Task2;
import Task.Repository.TaskRepository;

import java.time.LocalDate;

public class TaskService {
    private final TaskRepository repository;


    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task2 createTask(String title, String body, Priority priority, LocalDate dueDate){
        Task2 task = new Task2(title.trim(), body, priority, dueDate);
        return repository.save(task);
    }
}
