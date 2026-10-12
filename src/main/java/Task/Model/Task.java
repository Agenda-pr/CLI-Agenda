package Task.Model;

import java.util.UUID;

public class Task {
    private final UUID id;
    private String title;
    private String body;

    public Task(String title, String body) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.body = body;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
