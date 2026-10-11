package Task.CLI;

import Task.Service.TaskService;

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
}
