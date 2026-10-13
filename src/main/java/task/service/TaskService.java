package task.service;

import task.model.Priority;
import task.model.Task;
import task.repository.Repository;
import common.exception.InvalidTaskException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class TaskService {
    private final Repository repository;

    public TaskService(Repository repository) {
        this.repository = repository;
    }

    public Task createTask(String title, Priority priority, LocalDate dueDate){
       validateTitle(title);
        validateDueDate(dueDate);
        Task task = new Task(title.trim(), priority, dueDate);
        return repository.save(task);
    }

    public List<Task> listTasksByCreation(){
        return repository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .toList();
    }

    public void validateTitle(String title){
        if(title == null || title.isBlank()){
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
    }
    public void validateDueDate(LocalDate date){
        if (date!=null && date.isBefore(LocalDate.now())){
            throw new InvalidTaskException("Due date cannot be in the past");
        }
    }

}

