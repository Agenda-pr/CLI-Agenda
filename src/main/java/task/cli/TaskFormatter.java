package task.cli;

import common.utils.DateUtils;
import task.dto.TaskDto;

import java.util.List;

public class TaskFormatter {

    private TaskFormatter() {}

    public static String format(TaskDto taskDto) {
        String due = (taskDto.dueDate() == null)
                ? "without date"
                : DateUtils.formatDate(taskDto.dueDate());

        String completed = taskDto.completed() ? "completed" : "not completed";

        return taskDto.title()
                + " | Due: " + due
                + " | Priority: " + taskDto.priority()
                + " | Status: " + completed
                + " | Created: " + DateUtils.formatDateTime(taskDto.createdAt());
    }

    public static boolean printList(List<TaskDto> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            System.out.println(emptyMessage);
            return false;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + format(tasks.get(i)));
        }
        return true;
    }
}
