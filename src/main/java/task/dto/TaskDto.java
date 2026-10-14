package task.dto;

import task.model.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        Priority priority,
        boolean completed,
        LocalDate dueDate,
        LocalDateTime createdAt) {}

