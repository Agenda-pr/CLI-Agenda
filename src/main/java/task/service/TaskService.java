package task.service;

import common.exception.TaskNotFound;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import task.repository.TaskRepository;

import java.util.Comparator;
import java.util.List;
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
        task.setDueDate(TaskDueDate.create(taskDto.dueDate()));

        taskRepository.save(task);

        return toDto(task);
    }

    public void deleteTaskByID(UUID id){
        if (!taskRepository.deleteTaskByID(id)){
            throw new TaskNotFound("Task not found on repository");
        }
    }

}
