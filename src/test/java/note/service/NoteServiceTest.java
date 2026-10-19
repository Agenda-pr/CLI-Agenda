package note.service;

import common.exception.NoteContentEmpty;
import common.exception.NoteNotFound;
import common.exception.TaskNotFound;
import note.dto.CreateNoteDto;
import note.dto.NoteDto;
import note.repository.InRamNoteRepository;
import task.model.Priority;
import task.model.Task;
import task.model.TaskTitle;
import task.repository.InRamTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NoteServiceTest {
    private InRamNoteRepository noteRepository;
    private InRamTaskRepository taskRepository;
    private NoteService service;
    private Task existingTask;

    @BeforeEach
    void ini() {
        noteRepository = new InRamNoteRepository();
        taskRepository = new InRamTaskRepository();
        service = new NoteService(noteRepository, taskRepository);

        existingTask = Task.create(TaskTitle.of("Tarea Principal"), Priority.MEDIUM);
        taskRepository.save(existingTask);
    }

    @Test
    void createNoteSavesInRepository() {
        NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Nota de prueba"));

        assertEquals(1, noteRepository.showAllNotes().size());
        assertNotNull(created.id());
        assertNotNull(created.createdAt());
        assertEquals(existingTask.getId(), created.taskID());
        assertEquals("Nota de prueba", created.content());
    }

    @Test
    void createNoteTrimContent() {
        NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "   Contenido con espacios   "));

        assertEquals("Contenido con espacios", created.content());
    }

    @Test
    void createNoteThrowsTaskNotFoundWhenTaskDoesNotExist() {
        UUID nonExistentTaskId = UUID.randomUUID();

        assertThrows(TaskNotFound.class,
                () -> service.createNote(new CreateNoteDto(nonExistentTaskId, "Nota para tarea fantasma")));

        assertTrue(noteRepository.showAllNotes().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createNoteThrowsExceptionWhenContentIsInvalid(String invalidContent) {
        assertThrows(NoteContentEmpty.class,
                () -> service.createNote(new CreateNoteDto(existingTask.getId(), invalidContent)));

        assertTrue(noteRepository.showAllNotes().isEmpty());
    }

    @Nested
    class ListNotesFromTask {
        private NoteDto note1;
        private NoteDto note2;
        private NoteDto note3;

        @BeforeEach
        void set() {
            note1 = service.createNote(new CreateNoteDto(existingTask.getId(), "Primera nota"));
            note2 = service.createNote(new CreateNoteDto(existingTask.getId(), "Segunda nota"));
            note3 = service.createNote(new CreateNoteDto(existingTask.getId(), "Tercera nota"));
        }

        @Test
        void findNotesFromTaskSortByCreationDate() {
            List<NoteDto> notes = service.findNotesFromTask(existingTask.getId());

            assertEquals(List.of(note1, note2, note3), notes);
        }

        @Test
        void findNotesFromTaskReturnsOnlyNotesForThatTask() {
            Task anotherTask = Task.create(TaskTitle.of("Otra Tarea"), Priority.LOW);
            taskRepository.save(anotherTask);
            service.createNote(new CreateNoteDto(anotherTask.getId(), "Nota de otra tarea"));

            List<NoteDto> notesForExistingTask = service.findNotesFromTask(existingTask.getId());

            assertEquals(3, notesForExistingTask.size());
            assertTrue(notesForExistingTask.containsAll(List.of(note1, note2, note3)));
        }

        @Test
        void findNotesFromTaskReturnsEmptyListWhenTaskHasNoNotes() {
            Task emptyTask = Task.create(TaskTitle.of("Tarea sin notas"), Priority.LOW);
            taskRepository.save(emptyTask);

            List<NoteDto> notes = service.findNotesFromTask(emptyTask.getId());

            assertNotNull(notes);
            assertTrue(notes.isEmpty());
        }

        @Test
        void findNotesFromTaskThrowsTaskNotFoundWhenTaskDoesNotExist() {
            assertThrows(TaskNotFound.class, () -> service.findNotesFromTask(UUID.randomUUID()));
        }
    }

    @Nested
    class FindNoteById {
        @Test
        void findNoteByIdReturnsNote() {
            NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Nota para buscar"));

            NoteDto found = service.findNoteById(created.id());

            assertNotNull(found);
            assertEquals(created.id(), found.id());
            assertEquals(created.taskID(), found.taskID());
            assertEquals(created.content(), found.content());
        }

        @Test
        void findNoteByIdThrowsNoteNotFoundWhenUnknownId() {
            assertThrows(NoteNotFound.class, () -> service.findNoteById(UUID.randomUUID()));
        }
    }

    @Nested
    class UpdateNote {
        @Test
        void updateNoteModifiesContent() {
            NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Original"));

            NoteDto updatedDto = new NoteDto(
                    created.id(),
                    created.taskID(),
                    "Contenido Modificado",
                    created.createdAt()
            );

            NoteDto result = service.updateNote(updatedDto);

            assertEquals("Contenido Modificado", result.content());
            assertEquals(1, noteRepository.showAllNotes().size());
            assertEquals("Contenido Modificado", noteRepository.findNoteById(created.id()).getContent().content());
        }

        @Test
        void updateNoteTrimContent() {
            NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Original"));

            NoteDto updatedDto = new NoteDto(
                    created.id(),
                    created.taskID(),
                    "   Contenido con espacios   ",
                    created.createdAt()
            );

            NoteDto result = service.updateNote(updatedDto);

            assertEquals("Contenido con espacios", result.content());
        }

        @Test
        void updateNoteThrowsNoteNotFoundWhenUnknownId() {
            NoteDto nonExistent = new NoteDto(
                    UUID.randomUUID(),
                    existingTask.getId(),
                    "Texto",
                    LocalDateTime.now()
            );

            assertThrows(NoteNotFound.class, () -> service.updateNote(nonExistent));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void updateNoteThrowsExceptionWhenContentIsInvalid(String invalidContent) {
            NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Valida"));

            NoteDto invalidDto = new NoteDto(
                    created.id(),
                    created.taskID(),
                    invalidContent,
                    created.createdAt()
            );

            assertThrows(NoteContentEmpty.class, () -> service.updateNote(invalidDto));
        }
    }

    @Nested
    class DeleteNoteById {
        @Test
        void deleteNoteByIdRemovesNoteFromRepository() {
            NoteDto created = service.createNote(new CreateNoteDto(existingTask.getId(), "Nota a eliminar"));

            service.deleteNoteById(created.id());

            assertTrue(noteRepository.showAllNotes().isEmpty());
            assertNull(noteRepository.findNoteById(created.id()));
        }

        @Test
        void deleteNoteByIdThrowsNoteNotFoundWhenUnknownId() {
            assertThrows(NoteNotFound.class, () -> service.deleteNoteById(UUID.randomUUID()));
        }
    }
}
