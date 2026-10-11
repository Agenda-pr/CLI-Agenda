package Task.Service;

import Task.Model.Priority;
import Task.Model.Task2;
import Task.Repository.TaskRepository;
import common.exception.InvalidTaskException;

import java.time.LocalDate;

public class TaskService {
    private final TaskRepository repository;


    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task2 createTask(String title, String body, Priority priority, LocalDate dueDate){
        if(title == null || title.isBlank()){
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
        if (body == null || body.isBlank()){
            throw new InvalidTaskException("Task description cannot be empty or blank");
        }
        Task2 task = new Task2(title.trim(), body.trim(), priority, dueDate);
        return repository.save(task);
    }
}
