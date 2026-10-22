package task.cli;

import common.exception.InvalidTaskException;
import common.exception.TaskNotFound;
import common.utils.ConsoleReader;
import event.service.EventService;
import note.service.NoteService;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Priority;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import task.service.TaskService;
import java.util.List;

public class TaskCli {
    private final TaskService service;
    private final NoteService noteService;
    private final EventService eventService;

    public TaskCli(TaskService service, NoteService noteService, EventService eventService) {
        this.service = service;
        this.noteService = noteService;
        this.eventService = eventService;
    }

    public void showMenu() {
        boolean exitTask = false;
        while (!exitTask) {
            System.out.println("\n****TASKS****");
            System.out.println("1. Create task");
            System.out.println("2. List & filter tasks");
            System.out.println("3. Edit task");
            System.out.println("4. Delete task");
            System.out.println("0. Go back");

            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> createNewTask();
                case 2 -> listTasksMenu();
                case 3 -> editTask();
                case 4 -> deleteTask();
                case 0 -> exitTask = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createNewTask() {
        TaskTitle taskTitle = TaskInputReader.readTitle();
        Priority priority = TaskInputReader.readPriority(null);
        TaskDueDate dueDate = TaskInputReader.readDueDate();

        try {
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    taskTitle.title(),
                    priority,
                    dueDate != null ? dueDate.date() : null);

            TaskDto taskDto = service.createTask(createTaskDto);
            System.out.println("Task created successfully: " + taskDto.title());
        } catch (InvalidTaskException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void listTasksMenu() {
        System.out.println("\n--- LIST & FILTER TASKS ---");
        System.out.println("1. All tasks (by creation date)");
        System.out.println("2. All tasks (by due date proximity)");
        System.out.println("3. All tasks (by priority HIGH -> LOW)");
        System.out.println("4. Filter by status: Pending");
        System.out.println("5. Filter by status: Completed");
        System.out.println("6. Filter by specific priority");
        System.out.println("0. Cancel");

        int option = ConsoleReader.readInt("Choose an option: ");
        switch (option) {
            case 1 -> listTasks();
            case 2 -> TaskFormatter.printList(service.listTasksByDueDate(), "There isn't any task recorded.");
            case 3 -> TaskFormatter.printList(service.listTasksOrderedByPriority(), "There isn't any task recorded.");
            case 4 -> TaskFormatter.printList(service.listTasksByStatus(false), "No pending tasks found.");
            case 5 -> TaskFormatter.printList(service.listTasksByStatus(true), "No completed tasks found.");
            case 6 -> filterBySelectedPriority();
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void filterBySelectedPriority() {
        Priority priority = TaskInputReader.readPriority(null);
        if (priority == null) {
            System.out.println("Operation cancelled.");
            return;
        }
        TaskFormatter.printList(
                service.listTasksByPriority(priority),
                "No tasks found with priority " + priority + ".");
    }

    private boolean listTasks() {
        return TaskFormatter.printList(service.listTasksByCreation(), "There isn't any task recorded.");
    }

    private void editTask() {
        System.out.println("\n--- SELECT A TASK TO EDIT ---");
        TaskDto taskToEdit = listAndSelectTask();
        if (taskToEdit == null) {
            return;
        }

        TaskDto taskWithChanges = promptFieldToEdit(taskToEdit);
        if (taskWithChanges == null) {
            return;
        }

        try {
            TaskDto editedTask = service.updateTask(taskWithChanges);
            System.out.println("Task updated successfully:");
            System.out.println(TaskFormatter.format(editedTask));
        } catch (InvalidTaskException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private TaskDto promptFieldToEdit(TaskDto task) {
        System.out.println("\nSelected task:");
        System.out.println(TaskFormatter.format(task));
        System.out.println("1. Edit title");
        System.out.println("2. Edit priority");
        System.out.println("3. Edit due date");
        System.out.println("4. Toggle status (completed / not completed)");
        System.out.println("0. Cancel");

        int option = ConsoleReader.readInt("Choose an option: ");
        return switch (option) {
            case 1 -> new TaskDto(
                    task.id(),
                    TaskInputReader.readTitleForEdit(task.title()),
                    task.priority(),
                    task.completed(),
                    task.dueDate(),
                    task.createdAt());
            case 2 -> new TaskDto(
                    task.id(),
                    task.title(),
                    TaskInputReader.readPriority(task.priority()),
                    task.completed(),
                    task.dueDate(),
                    task.createdAt());
            case 3 -> new TaskDto(
                    task.id(),
                    task.title(),
                    task.priority(),
                    task.completed(),
                    TaskInputReader.readDueDateForEdit(task.dueDate()),
                    task.createdAt());
            case 4 -> new TaskDto(
                    task.id(),
                    task.title(),
                    task.priority(),
                    !task.completed(),
                    task.dueDate(),
                    task.createdAt());
            case 0 -> {
                System.out.println("Edit cancelled.");
                yield null;
            }
            default -> {
                System.out.println("Invalid option.");
                yield null;
            }
        };
    }

    private void deleteTask() {
        System.out.println("\n--- SELECT A TASK TO DELETE ---");
        TaskDto taskToDelete = listAndSelectTask();
        if (taskToDelete == null) {
            return;
        }

        System.out.println("\nSelected task:");
        System.out.println(TaskFormatter.format(taskToDelete));

        if (!ConsoleReader.confirmAction("Are you sure you want to delete this task?")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            service.deleteTaskByID(taskToDelete.id());
            System.out.println("Task deleted successfully.");
        } catch (TaskNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private TaskDto listAndSelectTask() {
        List<TaskDto> tasks = service.listTasksByCreation();
        if (!TaskFormatter.printList(tasks, "There isn't any task recorded.")) {
            return null;
        }
        return TaskInputReader.selectTaskFromList(tasks);
    }
}
