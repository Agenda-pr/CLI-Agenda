import Task.CLI.TaskCli;
import Task.Repository.TaskRepository;
import Task.Service.TaskService;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = task -> {
            System.out.println("[REPO] SAVED -> id=" + task.getId()
                    + ", title=" + task.getTitle()
                    + ", body=" + task.getBody()
                    + ", priority=" + task.getPriority()
                    + ", completed=" + task.isCompleted()
                    + ", dueDate=" + task.getDueDate()
                    + ", createdAt=" + task.getCreatedAt());
            return task;
        };
        new TaskCli(new TaskService(repository)).showMenu();
    }
}
