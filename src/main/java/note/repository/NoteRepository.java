package note.repository;

import note.model.Note;
import note.model.NoteTaskID;

import java.util.List;
import java.util.UUID;

public interface NoteRepository {
    void saveNote(Note note);
    List<Note> showAllNotes();
    Note findNoteById(UUID id);
    boolean deleteNoteById(UUID id);
    List<Note> findNotesByTaskId(NoteTaskID id);
}
