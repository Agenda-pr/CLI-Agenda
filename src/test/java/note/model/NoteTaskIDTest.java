package note.model;

import common.exception.TaskIDNull;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NoteTaskIDTest {

    @Test
    void createsValidNoteTaskId() {
        UUID uuid = UUID.randomUUID();
        NoteTaskID noteTaskId = NoteTaskID.of(uuid);

        assertNotNull(noteTaskId);
        assertEquals(uuid, noteTaskId.id());
    }

    @Test
    void throwsExceptionWhenTaskIdIsNull() {
        assertThrows(TaskIDNull.class, () -> NoteTaskID.of(null));
    }

    @Test
    void twoNoteTaskIdsWithSameValueAreEqual() {
        UUID uuid = UUID.randomUUID();
        NoteTaskID id1 = NoteTaskID.of(uuid);
        NoteTaskID id2 = NoteTaskID.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
