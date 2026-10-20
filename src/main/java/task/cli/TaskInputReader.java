package task.cli;

import common.exception.InvalidTaskException;
import common.utils.ConsoleReader;
import common.utils.DateUtils;
import task.dto.TaskDto;
import task.model.Priority;
import task.model.TaskDueDate;
import task.model.TaskTitle;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TaskInputReader {

    private TaskInputReader() {}

    public static TaskTitle readTitle() {
        while (true) {
            String title = ConsoleReader.readString("Title: ");
            try {
                return TaskTitle.of(title);
            } catch (InvalidTaskException error) {
                System.out.println("Title cannot be empty. Try again.");
            }
        }
    }

    public static String readTitleForEdit(String current) {
        while (true) {
            String input = ConsoleReader.readString("Title [current: " + current + "] (or ENTER to keep): ");
            if (input.isEmpty()) {
                return current;
            }
            try {
                return TaskTitle.of(input).title();
            } catch (InvalidTaskException error) {
                System.out.println("Title cannot be empty. Try again.");
            }
        }
    }

    public static Priority readPriority(Priority current) {
        String currentText = (current == null) ? "" : " [current: " + current + "]";
        while (true) {
            String input = ConsoleReader.readString("Priority 1.High 2.Medium 3.Low" + currentText + ": ");
            switch (input) {
                case "" -> {
                    return current;
                }
                case "1" -> {
                    return Priority.HIGH;
                }
                case "2" -> {
                    return Priority.MEDIUM;
                }
                case "3" -> {
                    return Priority.LOW;
                }
                default -> System.out.println("Invalid option. Choose 1, 2, 3 or ENTER to continue.");
            }
        }
    }

    public static TaskDueDate readDueDate() {
        while (true) {
            LocalDate date = ConsoleReader
                    .readDateOptional("Optional. Due date " + DateUtils.DATE_PATTERN + ": ")
                    .orElse(null);

            try {
                return TaskDueDate.create(date);
            } catch (InvalidTaskException error) {
                System.out.println("Due date cannot be in the past. Try again.");
            }
        }
    }

    public static LocalDate readDueDateForEdit(LocalDate current) {
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
                TaskDueDate.create(date);
                return date;
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date. Use: " + DateUtils.DATE_PATTERN);
            } catch (InvalidTaskException error) {
                System.out.println("Due date cannot be in the past. Try again.");
            }
        }
    }

    public static TaskDto selectTaskFromList(List<TaskDto> tasks) {
        while (true) {
            String input = ConsoleReader.readString(
                    "\nSelect task number (1-" + tasks.size() + ", or ENTER/0 to cancel): ");
            if (input.isBlank() || input.equals("0")) {
                return null;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 1 && index <= tasks.size()) {
                    return tasks.get(index - 1);
                }
                System.out.println("Invalid number. Choose between 1 and " + tasks.size() + ".");
            } catch (NumberFormatException error) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}
