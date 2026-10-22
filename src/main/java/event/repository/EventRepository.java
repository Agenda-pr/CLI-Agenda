package event.repository;

import event.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventRepository {
    void save(Event event);
    List<Event> showAllEvents();
    Event findEventById(UUID id);
    boolean deleteEventById(UUID id);
    // void removeTaskFromAllEvents(UUID taskId);
}
