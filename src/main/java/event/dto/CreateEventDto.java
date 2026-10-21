package event.dto;

import event.model.EventRecurrenceType;

import java.time.LocalDate;

public record CreateEventDto(
        String title,
        LocalDate celebDate,
        EventRecurrenceType eventRecurrenceType) {}
