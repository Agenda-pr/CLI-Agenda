package event.cli;

import common.exception.EventNotFound;
import common.exception.InvalidEventException;
import common.exception.InvalidTaskException;
import common.exception.TaskNotFound;
import common.utils.ConsoleReader;
import event.dto.CreateEventDto;
import event.dto.EventDto;
import event.model.EventCelebDate;
import event.model.EventRecurrenceType;
import event.model.EventTitle;
import event.service.EventService;
import task.cli.TaskFormatter;
import task.cli.TaskInputReader;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Priority;
import task.model.TaskDueDate;
import task.model.TaskTitle;
import task.service.TaskService;

import java.util.List;

public class EventCli {
    private static final int UPCOMING_DAYS = 7;

    private final EventService eventService;
    private final TaskService taskService;

    public EventCli(EventService eventService, TaskService taskService) {
        this.eventService = eventService;
        this.taskService = taskService;
    }

    public void showMenu() {
        EventFormatter.printUpcomingNotifications(eventService.listUpcomingEvents(UPCOMING_DAYS));

        boolean exit = false;
        while (!exit) {
            System.out.println("\n****EVENTS****");
            System.out.println("1. Create event");
            System.out.println("2. List events");
            System.out.println("3. Edit event");
            System.out.println("4. Manage event tasks");
            System.out.println("5. Delete event");
            System.out.println("0. Go back");

            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> createNewEvent();
                case 2 -> listEventsMenu();
                case 3 -> editEvent();
                case 4 -> manageEventTasksMenu();
                case 5 -> deleteEvent();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createNewEvent() {
        EventTitle title = EventInputReader.readTitle();
        EventCelebDate celebDate = EventInputReader.readCelebDate();
        EventRecurrenceType recurrence = EventInputReader.readRecurrenceType(null);

        try {
            CreateEventDto createDto = new CreateEventDto(
                    title.title(),
                    celebDate.date(),
                    recurrence);

            EventDto created = eventService.createEvent(createDto);
            System.out.println("Event created successfully:");
            System.out.println(EventFormatter.format(created));
        } catch (InvalidEventException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void listEventsMenu() {
        System.out.println("\n--- LIST EVENTS ---");
        System.out.println("1. All events (by celebration date proximity)");
        System.out.println("2. All events (by creation date)");
        System.out.println("3. Upcoming events (next " + UPCOMING_DAYS + " days)");
        System.out.println("0. Cancel");

        int option = ConsoleReader.readInt("Choose an option: ");
        switch (option) {
            case 1 -> EventFormatter.printList(eventService.listEventsByDate(), "There isn't any event recorded.");
            case 2 -> EventFormatter.printList(eventService.listEventsByCreation(), "There isn't any event recorded.");
            case 3 -> EventFormatter.printUpcomingNotifications(eventService.listUpcomingEvents(UPCOMING_DAYS));
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void editEvent() {
        System.out.println("\n--- SELECT AN EVENT TO EDIT ---");
        EventDto eventToEdit = listAndSelectEvent();
        if (eventToEdit == null) {
            return;
        }

        EventDto eventWithChanges = promptFieldToEdit(eventToEdit);
        if (eventWithChanges == null) {
            return;
        }

        try {
            EventDto updated = eventService.updateEvent(eventWithChanges);
            System.out.println("Event updated successfully:");
            System.out.println(EventFormatter.format(updated));
        } catch (InvalidEventException | EventNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private EventDto promptFieldToEdit(EventDto event) {
        System.out.println("\nSelected event:");
        System.out.println(EventFormatter.format(event));
        System.out.println("1. Edit title");
        System.out.println("2. Edit celebration date");
        System.out.println("3. Edit recurrence");
        System.out.println("0. Cancel");

        int option = ConsoleReader.readInt("Choose an option: ");
        return switch (option) {
            case 1 -> new EventDto(
                    event.id(),
                    EventInputReader.readTitleForEdit(event.title()),
                    event.celebDate(),
                    event.eventRecurrenceType(),
                    event.createdAt(),
                    event.taskIds());
            case 2 -> new EventDto(
                    event.id(),
                    event.title(),
                    EventInputReader.readCelebDateForEdit(event.celebDate()),
                    event.eventRecurrenceType(),
                    event.createdAt(),
                    event.taskIds());
            case 3 -> new EventDto(
                    event.id(),
                    event.title(),
                    event.celebDate(),
                    EventInputReader.readRecurrenceType(event.eventRecurrenceType()),
                    event.createdAt(),
                    event.taskIds());
            case 0 -> {
                System.out.println("Edit cancelled.");
                yield null;
            }
            default -> {
                System.out.println("Invalid option.");
                yield null;
            }
        };
    }

    private void manageEventTasksMenu() {
        System.out.println("\n--- SELECT AN EVENT TO MANAGE ITS TASKS ---");
        EventDto event = listAndSelectEvent();
        if (event == null) {
            return;
        }

        System.out.println("\nEvent: " + EventFormatter.format(event));
        System.out.println("1. Add task to event");
        System.out.println("2. List tasks of event");
        System.out.println("3. Remove task from event");
        System.out.println("0. Cancel");

        int option = ConsoleReader.readInt("Choose an option: ");
        switch (option) {
            case 1 -> addTaskToEvent(event);
            case 2 -> listTasksOfEvent(event);
            case 3 -> removeTaskFromEvent(event);
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void addTaskToEvent(EventDto event) {
        System.out.println("\n--- NEW TASK FOR EVENT [" + event.title() + "] ---");
        TaskTitle taskTitle = TaskInputReader.readTitle();
        Priority priority = TaskInputReader.readPriority(null);
        TaskDueDate dueDate = TaskInputReader.readDueDate();

        try {
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    taskTitle.title(),
                    priority,
                    dueDate != null ? dueDate.date() : null);

            TaskDto createdTask = taskService.createTask(createTaskDto);
            eventService.addTaskToEvent(event.id(), createdTask.id());
            System.out.println("Task created and added to event successfully: " + createdTask.title());
        } catch (InvalidTaskException | EventNotFound | TaskNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void listTasksOfEvent(EventDto event) {
        List<TaskDto> eventTasks = getTasksForEvent(event);
        System.out.println("\nTasks for event [" + event.title() + "]:");
        TaskFormatter.printList(eventTasks, "This event has no tasks associated.");
    }

    private void removeTaskFromEvent(EventDto event) {
        List<TaskDto> eventTasks = getTasksForEvent(event);
        System.out.println("\nTasks for event [" + event.title() + "]:");
        if (!TaskFormatter.printList(eventTasks, "This event has no tasks associated.")) {
            return;
        }

        TaskDto taskToRemove = TaskInputReader.selectTaskFromList(eventTasks);
        if (taskToRemove == null) {
            return;
        }

        System.out.println("\nSelected task:");
        System.out.println(TaskFormatter.format(taskToRemove));

        if (!ConsoleReader.confirmAction("Are you sure you want to remove this task from the event?")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            eventService.removeTaskFromEvent(event.id(), taskToRemove.id());
            System.out.println("Task removed from event successfully.");
        } catch (EventNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private List<TaskDto> getTasksForEvent(EventDto event) {
        return taskService.listTasksByCreation().stream()
                .filter(task -> event.taskIds().contains(task.id()))
                .toList();
    }

    private void deleteEvent() {
        System.out.println("\n--- SELECT AN EVENT TO DELETE ---");
        EventDto eventToDelete = listAndSelectEvent();
        if (eventToDelete == null) {
            return;
        }

        System.out.println("\nSelected event:");
        System.out.println(EventFormatter.format(eventToDelete));

        if (!ConsoleReader.confirmAction("Are you sure you want to delete this event?")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            eventService.deleteEventById(eventToDelete.id());
            System.out.println("Event deleted successfully.");
        } catch (EventNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private EventDto listAndSelectEvent() {
        List<EventDto> events = eventService.listEventsByDate();
        if (!EventFormatter.printList(events, "There isn't any event recorded.")) {
            return null;
        }
        return EventInputReader.selectEventFromList(events);
    }
}
