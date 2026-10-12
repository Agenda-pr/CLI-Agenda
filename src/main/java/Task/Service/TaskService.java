package Task.Service;

import Task.Model.Priority;
import Task.Model.Task;
import Task.Repository.Repository;
import common.exception.InvalidTaskException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
        if (dueDate!=null && dueDate.isBefore(LocalDate.now())){
            throw new InvalidTaskException("Due date cannot be in the past");
        }
        Task task = new Task(title.trim(), priority, dueDate);
        return repository.save(task);
    }

    public List<Task> listTasksForCreation(){
        return repository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .toList();
    }

//    public String printAllTasks(){
//        return this.repository.showAllTasks().stream()
//                .map(Task::getTitle)
//                .collect(Collectors.joining("\n"));
//    }
}

