package event.dto;

import event.model.EventRecurrenceType;

import java.time.LocalDate;
import java.util.UUID;

public record EventDto(
        UUID id,
        String title,
        LocalDate celebDate,
        EventRecurrenceType eventRecurrenceType,
        LocalDate createdAt) {}
