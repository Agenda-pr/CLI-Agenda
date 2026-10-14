package task.dto;

import task.model.Priority;

import java.time.LocalDate;

public record CreateTaskDto(
        String title,
        Priority priority,
        LocalDate dueDate) {}

