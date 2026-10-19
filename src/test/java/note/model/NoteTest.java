package note.model;

import common.exception.NoteContentEmpty;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {

    @Test
    void createSetsCorrectDefaultsAndValues() {
        NoteTaskID taskId = NoteTaskID.of(UUID.randomUUID());
        NoteContent content = NoteContent.of("Recordar adjuntar reporte");
        Note note = Note.create(taskId, content);

        assertNotNull(note.getId());
        assertNotNull(note.getCreatedAt());
        assertEquals(taskId, note.getTaskID());
        assertEquals(content, note.getContent());
    }

    @Test
    void differentNotesHaveDifferentId() {
        NoteTaskID taskId = NoteTaskID.of(UUID.randomUUID());
        Note note1 = Note.create(taskId, NoteContent.of("Nota 1"));
        Note note2 = Note.create(taskId, NoteContent.of("Nota 2"));

        assertNotEquals(note1.getId(), note2.getId());
    }

    @Test
    void restoreReconstitutesNoteExactly() {
        UUID id = UUID.randomUUID();
        NoteTaskID taskId = NoteTaskID.of(UUID.randomUUID());
        NoteContent content = NoteContent.of("Nota histórica reconstituida");
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 15, 10, 30);

        Note note = Note.restore(id, taskId, content, createdAt);

        assertEquals(id, note.getId());
        assertEquals(taskId, note.getTaskID());
        assertEquals(content, note.getContent());
        assertEquals(createdAt, note.getCreatedAt());
    }

    @Test
    void setContentUpdatesContentSuccessfully() {
        Note note = Note.create(NoteTaskID.of(UUID.randomUUID()), NoteContent.of("Original"));
        NoteContent updated = NoteContent.of("Actualizado");

        note.setContent(updated);

        assertEquals(updated, note.getContent());
    }

    @Test
    void setContentWithNullThrowsNoteContentEmpty() {
        Note note = Note.create(NoteTaskID.of(UUID.randomUUID()), NoteContent.of("Original"));

        assertThrows(NoteContentEmpty.class, () -> note.setContent(null));
    }

    @Test
    void setContentFromTextUpdatesContentSuccessfully() {
        Note note = Note.create(NoteTaskID.of(UUID.randomUUID()), NoteContent.of("Original"));

        note.setContentFromText("Nuevo texto desde String");

        assertEquals("Nuevo texto desde String", note.getContent().content());
    }

    @Test
    void setContentFromTextWithInvalidTextThrowsNoteContentEmpty() {
        Note note = Note.create(NoteTaskID.of(UUID.randomUUID()), NoteContent.of("Original"));

        assertThrows(NoteContentEmpty.class, () -> note.setContentFromText("   "));
    }
}
