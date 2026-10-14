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
