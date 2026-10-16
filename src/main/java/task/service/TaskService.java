package task.service;

import common.exception.TaskNotFound;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Task;
import task.repository.Repository;
import common.exception.InvalidTaskException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TaskService {
    private final Repository repository;

    public TaskService(Repository repository) {
        this.repository = repository;
    }

    public TaskDto createTask(CreateTaskDto createTaskDto) {
        validateTitle(createTaskDto.title());
        validateDueDate(createTaskDto.dueDate());

        Task task = Task.create(
                createTaskDto.title().trim(),
                createTaskDto.priority(),
                createTaskDto.dueDate());
        repository.save(task);

        return toDto(task);
    }

    public List<TaskDto> listTasksByCreation() {
        return repository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Task title cannot be empty or blank");
        }
    }

    public void validateDueDate(LocalDate date) {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new InvalidTaskException("Due date cannot be in the past");
        }
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.isCompleted(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    public TaskDto findTaskById (UUID id){
        Task task = repository.findTask(id);

        if (task == null){
            throw new TaskNotFound("Task was not found ");
        }

        return this.toDto(task);
    }

    public TaskDto updateTask(TaskDto taskDto) {
        Task task = repository.findTask(taskDto.id());

        if (task == null) {
            throw new TaskNotFound("Task was not found");
        }

        validateTitle(taskDto.title());
        validateDueDate(taskDto.dueDate());

        task.setTitle(taskDto.title().trim());
        task.setPriority(taskDto.priority());
        task.setCompleted(taskDto.completed());
        task.setDueDate(taskDto.dueDate());

        repository.save(task);

        return toDto(task);
    }




}
