package event.model;

import java.time.LocalDate;
import java.util.UUID;

public class Event {

    private final UUID id;
    private EventTitle title;
    private EventCelebDate celebDate;
    private EventRecurrenceType eventRecurrenceType;
    private final LocalDate createdAt;

    private Event(UUID id, EventTitle title, EventCelebDate celebDate, EventRecurrenceType eventRecurrenceType, LocalDate createdAt) {
        this.id = id;
        this.title = title;
        this.celebDate = celebDate;
        this.eventRecurrenceType = (eventRecurrenceType != null) ? eventRecurrenceType : EventRecurrenceType.NONE;
        this.createdAt = createdAt;
    }

    public static Event create(EventTitle title, EventCelebDate celebDate, EventRecurrenceType eventRecurrenceType) {
        return new Event(UUID.randomUUID(), title, celebDate, eventRecurrenceType, LocalDate.now());
    }

    public static Event create(EventTitle title, EventCelebDate celebDate) {
        return create(title, celebDate, EventRecurrenceType.NONE);
    }

    public static Event restore(UUID id, EventTitle title, EventCelebDate celebDate, EventRecurrenceType eventRecurrenceType, LocalDate createdAt) {
        return new Event(id, title, celebDate, eventRecurrenceType, createdAt);
    }
    // Cuando se celebre el evento, se chequea la periodicidad y si es distinta a none se vuelve a crear un VO de la fecha de celebración
    public void nextCelebDate() {
        LocalDate current = celebDate.date();
        switch (eventRecurrenceType) {
            case NONE -> {}
            case DAILY -> this.celebDate = EventCelebDate.of(current.plusDays(1));
            case WEEKLY -> this.celebDate = EventCelebDate.of(current.plusWeeks(1));
            case MONTHLY -> this.celebDate = EventCelebDate.of(current.plusMonths(1));
            case YEARLY -> this.celebDate = EventCelebDate.of(current.plusYears(1));
        }
    }

    public UUID getId() {
        return id;
    }

    public EventTitle getTitle() {
        return title;
    }

    public EventCelebDate getCelebDate() {
        return celebDate;
    }

    public EventRecurrenceType getEventRecurrenceType() {
        return eventRecurrenceType;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setTitle(EventTitle title) {
        this.title = title;
    }

    public void setCelebDate(EventCelebDate celebDate) {
        this.celebDate = celebDate;
    }

    public void setEventRecurrenceType(EventRecurrenceType eventRecurrenceType) {
        this.eventRecurrenceType = (eventRecurrenceType != null) ? eventRecurrenceType : EventRecurrenceType.NONE;
    }
}
