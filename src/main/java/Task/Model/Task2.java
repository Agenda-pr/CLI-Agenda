package Task.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task2 {
    private final UUID id;
    private String title;
    private String body;
    private Priority priority;
    private boolean completed;
    private LocalDate dueDate;
    private final LocalDateTime createdAt;

    public Task2(String title, String body, Priority priority, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.body = body;
        this.priority = priority;
        this.completed = false;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }

    public Task2(String title, String body, Priority priority) {
        this(title, body, priority, null);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
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

    public void setBody(String body) {
        this.body = body;
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
