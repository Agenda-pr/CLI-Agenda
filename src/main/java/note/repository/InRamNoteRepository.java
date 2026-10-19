package note.repository;

import note.model.Note;
import note.model.NoteTaskID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InRamNoteRepository implements NoteRepository{
    private final List<Note> noteList = new ArrayList<>();

    @Override
    public void saveNote(Note note) {
        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).getId().equals(note.getId())) {
                noteList.set(i, note);
                return;
            }
        }
        noteList.add(note);
    }

    @Override
    public List<Note> showAllNotes() {
        return noteList.stream()
                .map(this::restoreFrom)
                .toList();
    }

    @Override
    public Note findNoteById(UUID id) {
        return noteList.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(this::restoreFrom)
                .orElse(null);
    }

    @Override
    public boolean deleteNoteById(UUID id) {
        return noteList.removeIf(note -> note.getId().equals(id));
    }

    @Override
    public List<Note> findNotesByTaskId(NoteTaskID id) {
        return noteList.stream()
                .filter(n -> n.getTaskID().equals(id))
                .map(this::restoreFrom)
                .toList();
    }

    private Note restoreFrom(Note note) {
        return Note.restore(
                note.getId(),
                note.getTaskID(),
                note.getContent(),
                note.getCreatedAt());
    }
}
