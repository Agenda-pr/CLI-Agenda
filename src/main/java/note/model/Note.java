package note.model;
import common.exception.NoteContentEmpty;
import java.time.LocalDateTime;
import java.util.UUID;

public class Note {
    private final UUID id;
    private final NoteTaskID taskID;
    private NoteContent content;
    private final LocalDateTime createdAt;

    private Note(UUID id, NoteTaskID taskID, NoteContent content, LocalDateTime createdAt) {
        this.id = id;
        this.taskID = taskID;
        this.content = content;
        this.createdAt = createdAt;
    }
    public static Note create(NoteTaskID taskID, NoteContent content) {
        return new Note(UUID.randomUUID(), taskID, content, LocalDateTime.now());
    }
    public static Note restore(UUID id, NoteTaskID taskID, NoteContent content, LocalDateTime createdAt) {
        return new Note(id, taskID, content, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public NoteTaskID getTaskID() {
        return taskID;
    }

    public NoteContent getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setContent(NoteContent content) {
        if (content == null) {
            throw new NoteContentEmpty();
        }
        this.content = content;
    }
}
