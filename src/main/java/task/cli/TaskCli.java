package task.cli;

import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Priority;
import task.service.TaskService;
import common.exception.InvalidTaskException;
import common.exception.TaskNotFound;
import common.utils.ConsoleReader;
import common.utils.DateUtils;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class TaskCli {
    private final TaskService service;


    public TaskCli(TaskService service) {
        this.service = service;
    }

    public void showMenu(){
        boolean exitTask= false;
        while(!exitTask){
            System.out.println("\n****TASKS****");
            System.out.println("1. Create task");
            System.out.println("2. List tasks");
            System.out.println("3. Edit task");
            System.out.println("0. Go back");


            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option){
                case 1 -> createNewTask();
                case 2 -> listTasks();
                case 3 -> editTask();
                case 0 -> exitTask = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createNewTask(){
        String title = readTitle();
        Priority priority = readPriority();
        LocalDate dueDate = readDueDate();

        try{
            CreateTaskDto createTaskDto = new CreateTaskDto(title, priority, dueDate);
            TaskDto taskDto = service.createTask(createTaskDto);
            System.out.println("Task created successfully: "+ taskDto.title());
        } catch (InvalidTaskException error){
            System.out.println("Error: " + error.getMessage());
        }
    }

    private String readTitle() {
        while (true) {
            String title = ConsoleReader.readString("Title: ");
            try {
                service.validateTitle(title);
                return title;
            } catch (InvalidTaskException error) {
                System.out.println("Title cannot be empty. Try again.");
            }

        }
    }

    private Priority readPriority(){
        while (true){
            String input = ConsoleReader.readString("Priority 1.High 2.Medium 3.Low ");
            switch (input){
                case "" -> {return null;}
                case "1" -> {return Priority.HIGH;}
                case "2" -> {return Priority.MEDIUM;}
                case "3" -> {return Priority.LOW;}
                default -> System.out.println("Invalid option. Choose 1,2,3 or ENTER to continue");

            }
        }
    }

    private LocalDate readDueDate() {
        while (true) {
            LocalDate date = ConsoleReader
                    .readDateOptional("Optional. Due date " + DateUtils.DATE_PATTERN + ": ")
                    .orElse(null);

            try {
                service.validateDueDate(date);
                return date;
            } catch (InvalidTaskException error){
                System.out.println("Due date cannot be in the past. Try again.");

            }
    }
    }

    private void listTasks(){
        List<TaskDto> tasks = service.listTasksByCreation();
        if (tasks.isEmpty()){
            System.out.println("There isn't any task recorded. ");
            return;
        }
        tasks.forEach(taskDto -> System.out.println(formatedTask(taskDto)));
    }

    private String formatedTask (TaskDto taskDto){
        String due = (taskDto.dueDate() == null)
                ? "without date"
                :DateUtils.formatDate(taskDto.dueDate());

        String completed = taskDto.completed()? "completed" : "not completed";

        return "ID "+taskDto.id() + " | "
                + taskDto.title() + " | Due: " + due + " | Priority:"
                + taskDto.priority() + " | Status: " + completed + " | Created: "
                + DateUtils.formatDateTime(taskDto.createdAt());
    }
    private void editTask() {
        List<TaskDto> tasks = service.listTasksByCreation();

        if (tasks.isEmpty()) {
            System.out.println("There isn't any task recorded to edit.");
            return;
        }

        System.out.println("\n--- SELECT A TASK TO EDIT ---");
        tasks.forEach(task -> System.out.println(formatedTask(task)));


        String idInput = ConsoleReader.readString("\nEnter Task ID (or press ENTER to cancel): ");
        if (idInput.isBlank()) {
            return;
        }

        UUID id;
        try {
            id = UUID.fromString(idInput);
        } catch (IllegalArgumentException error) {
            System.out.println("Invalid ID format.");
            return;
        }

        try {
            TaskDto taskToEdit = service.findTaskById(id);

            System.out.println("\nCurrent values:");
            System.out.println(formatedTask(taskToEdit));
            System.out.println("(Press ENTER to keep the current value)");

            TaskDto taskWithChanges = editTaskSubMenu(taskToEdit);
            TaskDto editedTask = service.updateTask(taskWithChanges);

            System.out.println("Task updated successfully.");
            System.out.println(formatedTask(editedTask));
        } catch (TaskNotFound error) {
            System.out.println("No task found with that ID.");
        } catch (InvalidTaskException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private TaskDto editTaskSubMenu (TaskDto taskToEdit){
        boolean completed = readCompletedForEdit(taskToEdit.completed());
        Priority priority = readPriorityForEdit(taskToEdit.priority());
        LocalDate dueDate = readDueDateForEdit(taskToEdit.dueDate());

        return new TaskDto(
                taskToEdit.id(),
                taskToEdit.title(),
                priority,
                completed,
                dueDate,
                taskToEdit.createdAt());
    }

    private boolean readCompletedForEdit(boolean current) {
        String currentText = current ? "completed" : "not completed";
        while (true) {
            String input = ConsoleReader
                    .readString("Completed? (y/n) [current: " + currentText + "]: ")
                    .toLowerCase();
            switch (input) {
                case "" -> { return current; }
                case "y" -> { return true; }
                case "n" -> { return false; }
                default -> System.out.println("Invalid option. Type y, n or ENTER to keep the current value.");
            }
        }
    }

    private Priority readPriorityForEdit(Priority current) {
        while (true) {
            String input = ConsoleReader
                    .readString("Priority 1.High 2.Medium 3.Low [current: " + current + "]: ");
            switch (input) {
                case "" -> { return current; }
                case "1" -> { return Priority.HIGH; }
                case "2" -> { return Priority.MEDIUM; }
                case "3" -> { return Priority.LOW; }
                default -> System.out.println("Invalid option. Choose 1, 2, 3 or ENTER to keep the current value.");
            }
        }
    }

    private LocalDate readDueDateForEdit(LocalDate current) {
        String currentText = (current == null) ? "without date" : DateUtils.formatDate(current);
        while (true) {
            String input = ConsoleReader.readString("Due date " + DateUtils.DATE_PATTERN
                    + " ('-' to remove) [current: " + currentText + "]: ");
            if (input.isEmpty()) {
                return current;
            }
            if (input.equals("-")) {
                return null;
            }
            try {
                LocalDate date = DateUtils.parseDate(input);
                service.validateDueDate(date);
                return date;
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date. Use: " + DateUtils.DATE_PATTERN);
            } catch (InvalidTaskException error) {
                System.out.println("Due date cannot be in the past. Try again.");
            }
        }
    }

}
