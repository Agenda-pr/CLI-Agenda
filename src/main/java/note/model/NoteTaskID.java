package note.model;

import common.exception.TaskIDNull;
import java.util.UUID;

public record NoteTaskID(UUID id) {

    public NoteTaskID {
        if (id == null) {
            throw new TaskIDNull();
        }
    }
    public static NoteTaskID of(UUID id) {
        return new NoteTaskID(id);
    }
}
