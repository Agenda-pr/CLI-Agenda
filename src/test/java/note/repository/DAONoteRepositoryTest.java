package note.repository;

import note.dao.NoteDAOAdapter;
import note.model.Note;
import note.model.NoteContent;
import note.model.NoteTaskID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.dao.TaskDAO;
import task.dao.TaskDAOAdapter;
import task.model.Priority;
import task.model.Task;
import task.model.TaskTitle;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DAONoteRepositoryTest {
    private final NoteRepository repository = new DAONoteRepository(new NoteDAOAdapter());
    private final TaskDAO taskDao = new TaskDAOAdapter();
    private Task task;
    private Task otherTask;
    private Note note;

    @BeforeEach
    void setUp() {
        task = Task.create(TaskTitle.of("Repo test task"), Priority.MEDIUM);
        otherTask = Task.create(TaskTitle.of("Other repo test task"), Priority.LOW);
        taskDao.insert(task);
        taskDao.insert(otherTask);
        note = Note.create(NoteTaskID.of(task.getId()), NoteContent.of("Repo content"));
    }

    @AfterEach
    void cleanUp() {
        taskDao.deleteById(task.getId());
        taskDao.deleteById(otherTask.getId());
    }

    @Test
    void saveNewNoteInsertsIt() {
        repository.saveNote(note);

        Note found = repository.findNoteById(note.getId());

        assertNotNull(found);
        assertEquals(note.getContent(), found.getContent());
        assertEquals(note.getTaskID(), found.getTaskID());
    }

    @Test
    void saveExistingNoteUpdatesItWithoutDuplicating() {
        repository.saveNote(note);

        note.setContent(NoteContent.of("Changed content"));
        repository.saveNote(note);

        assertEquals("Changed content", repository.findNoteById(note.getId()).getContent().content());
        long copies = repository.showAllNotes().stream()
                .filter(n -> n.getId().equals(note.getId()))
                .count();
        assertEquals(1, copies);
    }

    @Test
    void findNoteByIdReturnsNullWhenNotFound() {
        assertNull(repository.findNoteById(UUID.randomUUID()));
    }

    @Test
    void findNotesByTaskIdReturnsOnlyNotesOfThatTask() {
        Note otherNote = Note.create(NoteTaskID.of(otherTask.getId()), NoteContent.of("Other"));
        repository.saveNote(note);
        repository.saveNote(otherNote);

        var notes = repository.findNotesByTaskId(task.getId());

        assertEquals(1, notes.size());
        assertEquals(note.getId(), notes.get(0).getId());
    }

    @Test
    void deleteNoteByIdRemovesNote() {
        repository.saveNote(note);

        assertTrue(repository.deleteNoteById(note.getId()));
        assertNull(repository.findNoteById(note.getId()));
        assertFalse(repository.deleteNoteById(UUID.randomUUID()));
    }
}
