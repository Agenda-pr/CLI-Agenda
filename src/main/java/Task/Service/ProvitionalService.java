package Task.Service;

import Task.Model.Task;
import Task.Repository.Repository;

import java.util.stream.Collectors;

public class ProvitionalService {
    private Repository repo;

    public ProvitionalService(Repository repo) {
        this.repo = repo;
    }

    public String addTask(Task task){
        return this.repo.addTask(task);
    }

    public String printAllTasks(){
        return this.repo.showAllTasks().stream()
                .map(Task::getTitle)
                .collect(Collectors.joining("\n"));
    }
}
