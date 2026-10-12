package Task.Service;

import Task.Model.Priority;
import Task.Model.Task;
import Task.Repository.Repository;
import common.exception.InvalidTaskException;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class TaskService {
    private final Repository repository;


    public TaskService(Repository repository) {
        this.repository = repository;
    }

    public Task createTask(String title, Priority priority, LocalDate dueDate){
        if(title == null || title.isBlank()){
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
        Task task = new Task(title.trim(), priority, dueDate);
        return repository.save(task);
    }

    public String addTask(Task task){
        return this.repository.addTask(task);
    }

    public String printAllTasks(){
        return this.repository.showAllTasks().stream()
                .map(Task::getTitle)
                .collect(Collectors.joining("\n"));
    }
}

