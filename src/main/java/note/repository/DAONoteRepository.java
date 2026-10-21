package note.repository;

import note.dao.NoteDAO;
import note.model.Note;

import java.util.List;
import java.util.UUID;

public class DAONoteRepository implements NoteRepository {
    private final NoteDAO dao;

    public DAONoteRepository(NoteDAO dao){
        this.dao = dao;
    }
    @Override
    public void saveNote(Note note) {
        if (!dao.update(note)) {
            dao.insert(note);
        }
    }

    @Override
    public List<Note> showAllNotes() {
        return dao.findAll();
    }

    @Override
    public Note findNoteById(UUID id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public boolean deleteNoteById(UUID id) {
        return dao.deleteById(id);
    }

    @Override
    public List<Note> findNotesByTaskId(UUID taskId) {
        return dao.findAll().stream()
                .filter(note -> note.getTaskID().id().equals(taskId))
                .toList();
    }

    @Override
    public void deleteNotesByTaskId(UUID taskId) {
        findNotesByTaskId(taskId).forEach(note -> dao.deleteById(note.getId()));
    }
}
