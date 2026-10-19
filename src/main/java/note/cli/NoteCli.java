package note.cli;

import common.exception.NoteContentEmpty;
import common.exception.NoteNotFound;
import common.exception.TaskNotFound;
import common.utils.ConsoleReader;
import common.utils.DateUtils;
import note.dto.CreateNoteDto;
import note.dto.NoteDto;
import note.model.NoteContent;
import note.service.NoteService;
import task.dto.TaskDto;
import task.service.TaskService;

import java.util.List;
import java.util.UUID;

public class NoteCli {
    private final NoteService noteService;
    private final TaskService taskService;

    public NoteCli(NoteService noteService, TaskService taskService) {
        this.noteService = noteService;
        this.taskService = taskService;
    }

    public void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n****NOTES****");
            System.out.println("1. Create note");
            System.out.println("2. List notes of a task");
            System.out.println("3. Edit note");
            System.out.println("4. Delete note");
            System.out.println("0. Go back");

            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> createNewNote();
                case 2 -> listNotesOfTask();
                case 3 -> editNote();
                case 4 -> deleteNote();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createNewNote() {
        System.out.println("\n--- SELECT A TASK TO ADD A NOTE ---");
        if (!listTasks()) {
            return;
        }

        TaskDto task = selectTask();
        if (task == null) {
            return;
        }

        String content = readContent();

        try {
            CreateNoteDto createDto = new CreateNoteDto(task.id(), content);
            NoteDto created = noteService.createNote(createDto);
            System.out.println("Note created successfully.");
            System.out.println(formattedNote(created));
        } catch (NoteContentEmpty | TaskNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void listNotesOfTask() {
        System.out.println("\n--- SELECT A TASK TO LIST ITS NOTES ---");
        if (!listTasks()) {
            return;
        }

        TaskDto task = selectTask();
        if (task == null) {
            return;
        }

        System.out.println("\nNotes for task [" + task.title() + "]:");
        listNotes(task.id());
    }

    private void editNote() {
        System.out.println("\n--- SELECT A TASK TO EDIT ITS NOTES ---");
        if (!listTasks()) {
            return;
        }

        TaskDto task = selectTask();
        if (task == null) {
            return;
        }

        System.out.println("\nNotes for task [" + task.title() + "]:");
        if (!listNotes(task.id())) {
            return;
        }

        NoteDto noteToEdit = selectNote();
        if (noteToEdit == null) {
            return;
        }

        System.out.println("\nCurrent note:");
        System.out.println(formattedNote(noteToEdit));

        String newContent = readContentForEdit(noteToEdit.content());

        try {
            NoteDto updateDto = new NoteDto(
                    noteToEdit.id(),
                    noteToEdit.taskID(),
                    newContent,
                    noteToEdit.createdAt()
            );

            NoteDto updated = noteService.updateNote(updateDto);
            System.out.println("Note updated successfully.");
            System.out.println(formattedNote(updated));
        } catch (NoteContentEmpty | NoteNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void deleteNote() {
        System.out.println("\n--- SELECT A TASK TO DELETE A NOTE ---");
        if (!listTasks()) {
            return;
        }

        TaskDto task = selectTask();
        if (task == null) {
            return;
        }

        System.out.println("\nNotes for task [" + task.title() + "]:");
        if (!listNotes(task.id())) {
            return;
        }

        NoteDto noteToDelete = selectNote();
        if (noteToDelete == null) {
            return;
        }

        System.out.println("\nSelected note:");
        System.out.println(formattedNote(noteToDelete));

        if (!confirmDelete()) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            noteService.deleteNoteById(noteToDelete.id());
            System.out.println("Note deleted successfully.");
        } catch (NoteNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private boolean listTasks() {
        List<TaskDto> tasks = taskService.listTasksByCreation();
        if (tasks.isEmpty()) {
            System.out.println("There isn't any task recorded. ");
            return false;
        }
        tasks.forEach(taskDto -> System.out.println(formattedTask(taskDto)));
        return true;
    }

    private boolean listNotes(UUID taskId) {
        List<NoteDto> notes = noteService.findNotesFromTask(taskId);
        if (notes.isEmpty()) {
            System.out.println("There isn't any note recorded for this task.");
            return false;
        }
        notes.forEach(note -> System.out.println(formattedNote(note)));
        return true;
    }

    private String readContent() {
        while (true) {
            String content = ConsoleReader.readString("Content: ");
            try {
                NoteContent.of(content);
                return content;
            } catch (NoteContentEmpty error) {
                System.out.println("Content cannot be empty. Try again.");
            }
        }
    }

    private String readContentForEdit(String current) {
        while (true) {
            String input = ConsoleReader.readString("Content [current: " + current + "] (press ENTER to keep): ");
            if (input.isBlank()) {
                return current;
            }
            try {
                NoteContent.of(input);
                return input;
            } catch (NoteContentEmpty error) {
                System.out.println("Content cannot be empty. Try again.");
            }
        }
    }

    private TaskDto selectTask() {
        String idInput = ConsoleReader.readString("\nEnter Task ID (or press ENTER to cancel): ");
        if (idInput.isBlank()) {
            return null;
        }

        try {
            UUID id = UUID.fromString(idInput);
            return taskService.findTaskById(id);
        } catch (IllegalArgumentException error) {
            System.out.println("Invalid ID format.");
        } catch (TaskNotFound error) {
            System.out.println("No task found with that ID.");
        }
        return null;
    }

    private NoteDto selectNote() {
        String idInput = ConsoleReader.readString("\nEnter Note ID (or press ENTER to cancel): ");
        if (idInput.isBlank()) {
            return null;
        }

        try {
            UUID id = UUID.fromString(idInput);
            return noteService.findNoteById(id);
        } catch (IllegalArgumentException error) {
            System.out.println("Invalid ID format.");
        } catch (NoteNotFound error) {
            System.out.println("No note found with that ID.");
        }
        return null;
    }

    private boolean confirmDelete() {
        while (true) {
            String input = ConsoleReader
                    .readString("Are you sure you want to delete this note? (S/N): ")
                    .toLowerCase();
            switch (input) {
                case "s", "y" -> {
                    return true;
                }
                case "n" -> {
                    return false;
                }
                default -> System.out.println("Invalid option. Type S or N.");
            }
        }
    }

    private String formattedTask(TaskDto taskDto) {
        String due = (taskDto.dueDate() == null) ? "without date" : DateUtils.formatDate(taskDto.dueDate());

        String completed = taskDto.completed() ? "completed" : "not completed";

        return "ID " + taskDto.id() + " | "
                + taskDto.title() + " | Due: " + due + " | Priority:"
                + taskDto.priority() + " | Status: " + completed + " | Created: "
                + DateUtils.formatDateTime(taskDto.createdAt());
    }

    private String formattedNote(NoteDto note) {
        return "ID " + note.id()
                + " | Content: " + note.content()
                + " | Created: " + DateUtils.formatDateTime(note.createdAt());
    }
}
