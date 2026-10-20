package note.dao;

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
import common.exception.DatabaseException;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NoteDAOAdapterTest {
    private final NoteDAO dao = new NoteDAOAdapter();
    private final TaskDAO taskDao = new TaskDAOAdapter();
    private Task task;
    private Note note;

    @BeforeEach
    void setUp() {
        task = Task.create(TaskTitle.of("Note DAO test task"), Priority.MEDIUM);
        taskDao.insert(task);
        note = Note.create(NoteTaskID.of(task.getId()), NoteContent.of("Test content"));
    }

    @AfterEach
    void cleanUp() {
        taskDao.deleteById(task.getId());
    }

    @Test
    void insertAndFindByIdReturnsSameData() {
        dao.insert(note);
        Note found = dao.findById(note.getId()).orElseThrow();
        assertEquals(note.getId(), found.getId());
        assertEquals(note.getTaskID(), found.getTaskID());
        assertEquals(note.getContent(), found.getContent());
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        assertTrue(dao.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void updateChangesTheContent() {
        dao.insert(note);
        note.setContent(NoteContent.of("Changed content"));
        assertTrue(dao.update(note));
        Note found = dao.findById(note.getId()).orElseThrow();
        assertEquals("Changed content", found.getContent().content());
    }

    @Test
    void updateReturnsFalseWhenNoteDoesNotExist() {
        assertFalse(dao.update(note));
    }

    @Test
    void findAllContainsInsertedNote() {
        dao.insert(note);
        assertTrue(dao.findAll().stream()
                .anyMatch(n -> n.getId().equals(note.getId())));
    }

    @Test
    void deleteByIdRemovesNote() {
        dao.insert(note);
        assertTrue(dao.deleteById(note.getId()));
        assertTrue(dao.findById(note.getId()).isEmpty());
        assertFalse(dao.deleteById(UUID.randomUUID()));
    }

    @Test
    void insertForNonExistentTaskThrowsDatabaseException() {
        Note noteWithoutTask = Note.create(NoteTaskID.of(UUID.randomUUID()), NoteContent.of("Note without Task"));
        assertThrows(DatabaseException.class, () -> dao.insert(noteWithoutTask));
    }

    @Test
    void deletingTheTaskDeletesItsNotes() {
        dao.insert(note);
        taskDao.deleteById(task.getId());
        assertTrue(dao.findById(note.getId()).isEmpty());
    }
}
