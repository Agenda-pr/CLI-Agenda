import task.cli.TaskCli;
import task.repository.InRamRepository;
import task.service.TaskService;

public class Main {
    public static void main (String[] args){
        InRamRepository repository = new InRamRepository();
        TaskService service = new TaskService(repository);
        new TaskCli(service).showMenu();
    }
}

//import Task.CLI.TaskCli;
//import Task.Repository.Repository;
//import Task.Service.TaskService;
//
//public class Main {
//    public static void main(String[] args) {
//        Repository repository = task -> {
//            System.out.println("[REPO] SAVED -> id=" + task.getId()
//                    + ", title=" + task.getTitle()
//                    + ", body=" + task.getBody()
//                    + ", priority=" + task.getPriority()
//                    + ", completed=" + task.isCompleted()
//                    + ", dueDate=" + task.getDueDate()
//                    + ", createdAt=" + task.getCreatedAt());
//            return task;
//        };
//        new TaskCli(new TaskService(repository)).showMenu();
//    }
//}
