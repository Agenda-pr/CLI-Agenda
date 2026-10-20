package note.dao;

import note.model.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteDAO {
    void insert(Note note);
    boolean update(Note note);
    Optional<Note> findById(UUID id);
    List<Note> findAll();
    boolean deleteById(UUID id);
}
