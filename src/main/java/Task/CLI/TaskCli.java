package Task.CLI;

import Task.Model.Priority;
import Task.Model.Task;
import Task.Service.TaskService;
import common.exception.InvalidTaskException;
import common.utils.ConsoleReader;

import java.time.LocalDate;

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
            System.out.println("0. Go back");

            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option){
                case 1 -> createNewTask();
                case 0 -> exitTask = true;
                default -> System.out.println("Invalid option. Choose ");
            }
        }
    }

    private void createNewTask(){
        String title = ConsoleReader.readString("Title: ");
        Priority priority = readPriority();
        LocalDate dueDate = ConsoleReader
                .readDateOptional("Optional. Due date dd/mm/yyyy: ")
                .orElse(null);

        try{
            Task created= service.createTask(title,priority,dueDate);
            System.out.println("Task created successfully: "+ created.getTitle());
        } catch (InvalidTaskException error){
            System.out.println(("Error: " + error.getMessage()));
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
}
