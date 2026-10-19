import task.cli.TaskCli;
import task.repository.InRamTaskRepository;
import task.service.TaskService;

public class Main {
    public static void main (String[] args){
        InRamTaskRepository repository = new InRamTaskRepository();
        TaskService service = new TaskService(repository);
        new TaskCli(service).showMenu();
    }
}
