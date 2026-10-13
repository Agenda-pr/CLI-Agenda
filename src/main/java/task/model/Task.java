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

    public Task(String title, Priority priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.priority = (priority != null) ? priority: Priority.MEDIUM;
        this.completed = false;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }

    public Task(String title,  Priority priority) {
        this(title, priority, null);
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
