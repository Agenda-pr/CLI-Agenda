package task.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private final UUID id;
    private String title;
    private Priority priority;
    private boolean completed;
    private LocalDate dueDate;
    private final LocalDateTime createdAt;

    private Task(UUID id, String title, Priority priority, boolean completed, LocalDate dueDate, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.priority = (priority != null) ? priority : Priority.MEDIUM;
        this.completed = completed;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }


    public static Task create(String title, Priority priority, LocalDate dueDate) {
        return new Task(UUID.randomUUID(), title, priority, false, dueDate, LocalDateTime.now());
    }

    public static Task create(String title, Priority priority) {
        return create(title, priority, null);
    }

    public static Task restore(UUID id, String title, Priority priority, boolean completed, LocalDate dueDate, LocalDateTime createdAt) {
        return new Task(id, title, priority, completed, dueDate, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
