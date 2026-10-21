package event.service;

import common.exception.EventNotFound;
import common.exception.InvalidEventException;
import common.exception.TaskNotFound;
import event.dto.CreateEventDto;
import event.dto.EventDto;
import event.model.Event;
import event.model.EventCelebDate;
import event.model.EventRecurrenceType;
import event.model.EventTaskId;
import event.model.EventTitle;
import event.repository.EventRepository;
import task.model.Task;
import task.repository.TaskRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class EventService {
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    public EventService(EventRepository eventRepository, TaskRepository taskRepository) {
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
    }

    public EventDto createEvent(CreateEventDto createEventDto) {
        EventTitle title = EventTitle.of(createEventDto.title());
        EventCelebDate celebDate = EventCelebDate.of(createEventDto.celebDate());

        Event event = (createEventDto.eventRecurrenceType() == null)
                ? Event.create(title, celebDate)
                : Event.create(title, celebDate, createEventDto.eventRecurrenceType());
        eventRepository.save(event);

        return toDto(event);
    }

    public List<EventDto> listEventsByCreation() {
        return refreshRecurringEvents().stream()
                .sorted(Comparator.comparing(Event::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public List<EventDto> listEventsByDate() {
        return refreshRecurringEvents().stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(EventDto::celebDate))
                .toList();
    }

    public List<EventDto> listUpcomingEvents(int daysAhead) {
        if (daysAhead < 0) {
            throw new InvalidEventException("Days ahead cannot be negative");
        }
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(daysAhead);

        return refreshRecurringEvents().stream()
                .map(this::toDto)
                .filter(event -> !event.celebDate().isBefore(today) && !event.celebDate().isAfter(limitDate))
                .sorted(Comparator.comparing(EventDto::celebDate))
                .toList();
    }

    private List<Event> refreshRecurringEvents() {
        LocalDate today = LocalDate.now();
        List<Event> events = eventRepository.showAllEvents();
        for (Event event : events) {
            if (event.getEventRecurrenceType() != EventRecurrenceType.NONE
                    && event.getCelebDate().date().isBefore(today)) {
                while (event.getCelebDate().date().isBefore(today)) {
                    event.nextCelebDate();
                }
                eventRepository.save(event);
            }
        }
        return events;
    }

    public EventDto findEventById(UUID id) {
        Event event = eventRepository.findEventById(id);
        if (event == null) {
            throw new EventNotFound("Event was not found");
        }
        return toDto(event);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventRepository.findEventById(eventDto.id());
        if (event == null) {
            throw new EventNotFound("Event was not found");
        }

        event.setTitle(EventTitle.of(eventDto.title()));
        event.setCelebDate(EventCelebDate.of(eventDto.celebDate()));
        event.setEventRecurrenceType(eventDto.eventRecurrenceType());

        eventRepository.save(event);

        return toDto(event);
    }

    public EventDto addTaskToEvent(UUID eventId, UUID taskId) {
        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new EventNotFound("Event was not found");
        }

        Task task = taskRepository.findTaskByID(taskId);
        if (task == null) {
            throw new TaskNotFound("Task was not found");
        }

        event.addTaskId(EventTaskId.of(taskId));
        eventRepository.save(event);

        return toDto(event);
    }

    public EventDto removeTaskFromEvent(UUID eventId, UUID taskId) {
        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new EventNotFound("Event was not found");
        }

        event.removeTaskId(EventTaskId.of(taskId));
        eventRepository.save(event);

        return toDto(event);
    }

    public void deleteEventById(UUID id) {
        if (!eventRepository.deleteEventById(id)) {
            throw new EventNotFound("Event not found on repository");
        }
    }

    private EventDto toDto(Event event) {
        List<UUID> taskIds = event.getTaskIds().stream()
                .map(EventTaskId::id)
                .toList();

        return new EventDto(
                event.getId(),
                event.getTitle().title(),
                event.getCelebDate().date(),
                event.getEventRecurrenceType(),
                event.getCreatedAt(),
                taskIds);
    }
}
