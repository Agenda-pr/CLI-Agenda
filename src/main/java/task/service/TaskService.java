package task.service;

import common.exception.TaskNotFound;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import task.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TaskService {
    private final Repository repository;

    public TaskService(Repository repository) {
        this.repository = repository;
    }

    public TaskDto createTask(CreateTaskDto createTaskDto) {

        Task task = Task.create(
                TaskTitle.of(createTaskDto.title()),
                createTaskDto.priority(),
                TaskDueDate.create(createTaskDto.dueDate()));
        repository.save(task);

        return toDto(task);
    }

    public List<TaskDto> listTasksByCreation() {
        return repository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle().title(),
                task.getPriority(),
                task.isCompleted(),
                task.getDueDate() != null ? task.getDueDate().date() : null,
                task.getCreatedAt());
    }

    public TaskDto findTaskById(UUID id) {
        Task task = findTask(id);
        return this.toDto(task);
    }

    public TaskDto updateTask(TaskDto taskDto) {
        Task task = findTask(taskDto.id());

        task.setTitle(TaskTitle.of(taskDto.title()));
        task.setPriority(taskDto.priority());
        task.setCompleted(taskDto.completed());
        task.setDueDate(TaskDueDate.create(taskDto.dueDate()));

        repository.save(task);

        return toDto(task);
    }

    public void deleteTaskByID(UUID id) {
        if (!repository.deleteTaskByID(id)) {
            throw new TaskNotFound("Task not found on repository");
        }
    }

    public List<TaskDto> listCompletedTask() {
        return repository.listCompletedTasks().stream()
                .map(this::toDto)
                .toList();
    }

    public List<TaskDto> listPendingTask() {
        return repository.listPendingTasks().stream()
                .map(this::toDto)
                .toList();
    }

    public boolean markTaskAsCompleted(UUID id) {
        Task task = findTask(id);

        if (task.isCompleted()) {
            return false;
        }

        task.setCompleted(true);
        repository.save(task);
        return true;
    }

    private Task findTask(UUID id){
        Task task = repository.findTaskByID(id);
        if (task == null) {
            throw new TaskNotFound("Task was not found");
        }
        return task;
    }

}
