package event.cli;

import common.exception.InvalidEventException;
import common.utils.ConsoleReader;
import common.utils.DateUtils;
import event.dto.EventDto;
import event.model.EventCelebDate;
import event.model.EventRecurrenceType;
import event.model.EventTitle;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventInputReader {

    private EventInputReader() {}

    public static EventTitle readTitle() {
        while (true) {
            String title = ConsoleReader.readString("Title: ");
            try {
                return EventTitle.of(title);
            } catch (InvalidEventException error) {
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
                return EventTitle.of(input).title();
            } catch (InvalidEventException error) {
                System.out.println("Title cannot be empty. Try again.");
            }
        }
    }

    public static EventCelebDate readCelebDate() {
        while (true) {
            String input = ConsoleReader.readString("Celebration date " + DateUtils.DATE_PATTERN + ": ");
            try {
                LocalDate date = DateUtils.parseDate(input);
                return EventCelebDate.of(date);
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date. Use: " + DateUtils.DATE_PATTERN);
            } catch (InvalidEventException error) {
                System.out.println(error.getMessage() + ". Try again.");
            }
        }
    }

    public static LocalDate readCelebDateForEdit(LocalDate current) {
        String currentText = DateUtils.formatDate(current);
        while (true) {
            String input = ConsoleReader.readString(
                    "Celebration date " + DateUtils.DATE_PATTERN + " [current: " + currentText + "] (or ENTER to keep): ");
            if (input.isEmpty()) {
                return current;
            }
            try {
                LocalDate date = DateUtils.parseDate(input);
                return EventCelebDate.of(date).date();
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date. Use: " + DateUtils.DATE_PATTERN);
            } catch (InvalidEventException error) {
                System.out.println(error.getMessage() + ". Try again.");
            }
        }
    }

    public static EventRecurrenceType readRecurrenceType(EventRecurrenceType current) {
        String currentText = (current == null) ? "" : " [current: " + current + "]";
        while (true) {
            String input = ConsoleReader.readString(
                    "Recurrence 1.None 2.Daily 3.Weekly 4.Monthly 5.Yearly" + currentText + ": ");
            switch (input) {
                case "":
                    return current;
                case "1":
                    return EventRecurrenceType.NONE;
                case "2":
                    return EventRecurrenceType.DAILY;
                case "3":
                    return EventRecurrenceType.WEEKLY;
                case "4":
                    return EventRecurrenceType.MONTHLY;
                case "5":
                    return EventRecurrenceType.YEARLY;
                default:
                    System.out.println("Invalid option. Choose 1-5 or ENTER to continue.");
            }
        }
    }

    public static EventDto selectEventFromList(List<EventDto> events) {
        while (true) {
            String input = ConsoleReader.readString(
                    "\nSelect event number (1-" + events.size() + ", or ENTER/0 to cancel): ");
            if (input.isBlank() || input.equals("0")) {
                return null;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 1 && index <= events.size()) {
                    return events.get(index - 1);
                }
                System.out.println("Invalid number. Choose between 1 and " + events.size() + ".");
            } catch (NumberFormatException error) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}
