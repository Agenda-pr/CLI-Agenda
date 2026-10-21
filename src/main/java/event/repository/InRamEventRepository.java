package event.repository;

import event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InRamEventRepository implements EventRepository {
    private final List<Event> eventList = new ArrayList<>();

    @Override
    public void save(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(event.getId())) {
                eventList.set(i, event);
                return;
            }
        }
        eventList.add(event);
    }

    @Override
    public List<Event> showAllEvents() {
        return eventList.stream()
                .map(this::restoreFrom)
                .toList();
    }

    @Override
    public Event findEventById(UUID id) {
        return eventList.stream()
                .filter(event -> event.getId().equals(id))
                .findFirst()
                .map(this::restoreFrom)
                .orElse(null);
    }

    @Override
    public boolean deleteEventById(UUID id) {
        return eventList.removeIf(event -> event.getId().equals(id));
    }

    private Event restoreFrom(Event event) {
        return Event.restore(
                event.getId(),
                event.getTitle(),
                event.getCelebDate(),
                event.getEventRecurrenceType(),
                event.getCreatedAt(),
                event.getTaskIds()
        );
    }
}
