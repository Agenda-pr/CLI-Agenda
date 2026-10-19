package note.repository;

import note.model.Note;
import note.model.NoteContent;
import note.model.NoteTaskID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InRamNoteRepositoryTest {
    private InRamNoteRepository repository;
    private NoteTaskID taskId1;
    private NoteTaskID taskId2;
    private Note note1;
    private Note note2;

    @BeforeEach
    public void ini() {
        repository = new InRamNoteRepository();
        taskId1 = NoteTaskID.of(UUID.randomUUID());
        taskId2 = NoteTaskID.of(UUID.randomUUID());
        note1 = Note.create(taskId1, NoteContent.of("Note 1 content"));
        note2 = Note.create(taskId2, NoteContent.of("Note 2 content"));
    }

    @Test
    void saveReturnSameNote() {
        repository.saveNote(note1);

        Note saved = repository.findNoteById(note1.getId());
        assertNotNull(saved);
        assertEquals(note1.getId(), saved.getId());
        assertEquals(note1.getContent(), saved.getContent());
        assertEquals(note1.getTaskID(), saved.getTaskID());
    }

    @Test
    void saveAddNoteToList() {
        repository.saveNote(note1);

        List<Note> notes = repository.showAllNotes();
        assertEquals(1, notes.size());
        assertEquals(note1.getId(), notes.get(0).getId());
    }

    @Test
    void saveUpdatesExistingNoteWithoutDuplicating() {
        repository.saveNote(note1);
        note1.setContent(NoteContent.of("Note 1 Updated"));

        repository.saveNote(note1);

        assertEquals(1, repository.showAllNotes().size());
        assertEquals(NoteContent.of("Note 1 Updated"), repository.findNoteById(note1.getId()).getContent());
    }

    @Test
    void findNoteReturnsNoteByIDFromExistingId() {
        repository.saveNote(note2);

        Note found = repository.findNoteById(note2.getId());
        assertNotNull(found);
        assertEquals(note2.getId(), found.getId());
        assertEquals(note2.getContent(), found.getContent());
        assertEquals(note2.getTaskID(), found.getTaskID());
    }

    @Test
    void findNoteByIDReturnsNullUnknowId() {
        assertNull(repository.findNoteById(UUID.randomUUID()));
    }

    @Test
    void deleteNoteByIDReturnsTrueExistingId() {
        repository.saveNote(note1);

        boolean result = repository.deleteNoteById(note1.getId());

        assertTrue(result);
        assertNull(repository.findNoteById(note1.getId()));
        assertTrue(repository.showAllNotes().isEmpty());
    }

    @Test
    void deleteNoteByIDReturnsFalseUnknowId() {
        assertFalse(repository.deleteNoteById(UUID.randomUUID()));
    }

    @Test
    void findNotesByTaskIdReturnsAllNotesForTask() {
        Note note3 = Note.create(taskId1, NoteContent.of("Another note for task 1"));
        repository.saveNote(note1);
        repository.saveNote(note2);
        repository.saveNote(note3);

        List<Note> notesForTask1 = repository.findNotesByTaskId(taskId1.id());

        assertEquals(2, notesForTask1.size());
        assertTrue(notesForTask1.stream().anyMatch(n -> n.getId().equals(note1.getId())));
        assertTrue(notesForTask1.stream().anyMatch(n -> n.getId().equals(note3.getId())));
    }

    @Test
    void findNotesByTaskIdReturnsEmptyListForUnknownTask() {
        UUID unknownTaskId = UUID.randomUUID();

        List<Note> notes = repository.findNotesByTaskId(unknownTaskId);

        assertNotNull(notes);
        assertTrue(notes.isEmpty());
    }

    @Test
    void deleteNotesByTaskIdRemovesAllNotesForTargetTaskOnly() {
        Note note3 = Note.create(taskId1, NoteContent.of("Second note for task 1"));
        repository.saveNote(note1);
        repository.saveNote(note2);
        repository.saveNote(note3);

        repository.deleteNotesByTaskId(taskId1.id());

        assertTrue(repository.findNotesByTaskId(taskId1.id()).isEmpty());
        assertEquals(1, repository.showAllNotes().size());
        assertNotNull(repository.findNoteById(note2.getId()));
    }

    @Test
    void deleteNotesByTaskIdDoesNothingWhenTaskHasNoNotes() {
        repository.saveNote(note1);

        repository.deleteNotesByTaskId(UUID.randomUUID());

        assertEquals(1, repository.showAllNotes().size());
    }
}
