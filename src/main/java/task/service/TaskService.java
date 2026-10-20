package task.service;

import common.exception.InvalidTaskException;
import common.exception.TaskNotFound;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Priority;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import task.repository.TaskRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDto createTask(CreateTaskDto createTaskDto) {

        Task task = Task.create(
                TaskTitle.of(createTaskDto.title()),
                createTaskDto.priority(),
                TaskDueDate.create(createTaskDto.dueDate()));
        taskRepository.save(task);

        return toDto(task);
    }

    public List<TaskDto> listTasksByCreation() {
        return taskRepository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public List<TaskDto> listTasksByStatus(boolean completed) {
        return taskRepository.showAllTasks().stream()
                .filter(task -> task.isCompleted() == completed)
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public List<TaskDto> listTasksByPriority(Priority priority) {
        if (priority == null) {
            throw new InvalidTaskException("Priority cannot be null");
        }
        return taskRepository.showAllTasks().stream()
                .filter(task -> task.getPriority() == priority)
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public List<TaskDto> listTasksOrderedByPriority() {
        return taskRepository.showAllTasks().stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed().thenComparing(Task::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public List<TaskDto> listTasksByDueDate() {
        return taskRepository.showAllTasks().stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(TaskDto::dueDate, Comparator.nullsLast(Comparator.naturalOrder())))
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
        Task task = taskRepository.findTaskByID(id);

        if (task == null) {
            throw new TaskNotFound("Task was not found ");
        }

        return this.toDto(task);
    }

    public TaskDto updateTask(TaskDto taskDto) {
        Task task = taskRepository.findTaskByID(taskDto.id());

        if (task == null) {
            throw new TaskNotFound("Task was not found");
        }

        task.setTitle(TaskTitle.of(taskDto.title()));
        task.setPriority(taskDto.priority());
        task.setCompleted(taskDto.completed());

        LocalDate currentDueDate = task.getDueDate() != null ? task.getDueDate().date() : null;
        if (!Objects.equals(currentDueDate, taskDto.dueDate())) {
            task.setDueDate(TaskDueDate.create(taskDto.dueDate()));
        }

        taskRepository.save(task);

        return toDto(task);
    }

    public void deleteTaskByID(UUID id){
        if (!taskRepository.deleteTaskByID(id)){
            throw new TaskNotFound("Task not found on repository");
        }
    }

}
