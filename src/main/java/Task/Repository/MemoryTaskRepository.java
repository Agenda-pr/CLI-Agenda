//package Task.Repository;
//
//import Task.Model.Task2;
//
//import java.util.*;
//
//public class MemoryTaskRepository implements TaskRepository {
//    private final Map<UUID, Task2> tasks = new LinkedHashMap<>();
//
//    @Override
//    public Task2 save(Task2 task) {
//        tasks.put(task.getId(), task);
//        return task;
//    }
//
//    @Override
//    public List<Task2> findAll() {
//        return new ArrayList<>(tasks.values());
//    }
//
//    @Override
//    public boolean deleteById(UUID id) {
//        return tasks.remove(id) != null;
//    }
//
//}
