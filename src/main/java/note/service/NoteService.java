package note.service;

import common.exception.NoteNotFound;
import common.exception.TaskNotFound;
import note.dto.CreateNoteDto;
import note.dto.NoteDto;
import note.model.Note;
import note.model.NoteContent;
import note.model.NoteTaskID;
import note.repository.NoteRepository;
import task.model.Task;
import task.repository.TaskRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class NoteService {
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public NoteService(NoteRepository noteRepository, TaskRepository taskRepository) {
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }


    public NoteDto createNote(CreateNoteDto createNoteDto){
        Task task = taskRepository.findTaskByID(createNoteDto.taskID());
        if (task == null) {
            throw new TaskNotFound("Task not found with ID");
        }

        Note note = Note.create(
                NoteTaskID.of(createNoteDto.taskID()),
                NoteContent.of(createNoteDto.content()));
        noteRepository.saveNote(note);

        return toDto(note);
    }

    public List<NoteDto> findNotesFromTask(UUID taskId){
        Task task = taskRepository.findTaskByID(taskId);

        if (task == null) {
            throw new TaskNotFound("Task not found with ID");
        }

        return noteRepository.findNotesByTaskId(taskId).stream()
                .sorted(Comparator.comparing(Note::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    public NoteDto findNoteById(UUID id){
        Note note = noteRepository.findNoteById(id);
        if(note == null){
            throw new NoteNotFound();
        }
        return toDto(note);
    }

    public NoteDto updateNote(NoteDto updatedNote){
        Note noteToUpdate = noteRepository.findNoteById(updatedNote.id());
        if (noteToUpdate == null){
            throw new NoteNotFound();
        }

        noteToUpdate.setContent(NoteContent.of(updatedNote.content()));
        noteRepository.saveNote(noteToUpdate);

        return toDto(noteToUpdate);
    }

    public void deleteNoteById(UUID id){
        if(!noteRepository.deleteNoteById(id)){
            throw new NoteNotFound();
        }
    }

    private NoteDto toDto(Note note){
        return new NoteDto(
                note.getId(),
                note.getTaskID().id(),
                note.getContent().content(),
                note.getCreatedAt());
    }
}
