package note.cli;

import common.exception.NoteContentEmpty;
import common.exception.NoteNotFound;
import common.exception.TaskNotFound;
import common.utils.ConsoleReader;
import note.dto.CreateNoteDto;
import note.dto.NoteDto;
import note.service.NoteService;
import task.cli.TaskFormatter;
import task.cli.TaskInputReader;
import task.dto.TaskDto;
import task.service.TaskService;

import java.util.List;

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
        TaskDto task = listAndSelectTask();
        if (task == null) {
            return;
        }

        String content = NoteInputReader.readContent(null);

        try {
            CreateNoteDto createDto = new CreateNoteDto(task.id(), content);
            NoteDto created = noteService.createNote(createDto);
            System.out.println("Note created successfully.");
            System.out.println(NoteFormatter.format(created));
        } catch (NoteContentEmpty | TaskNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void listNotesOfTask() {
        System.out.println("\n--- SELECT A TASK TO LIST ITS NOTES ---");
        TaskDto task = listAndSelectTask();
        if (task == null) {
            return;
        }

        System.out.println("\nNotes for task [" + task.title() + "]:");
        NoteFormatter.printList(
                noteService.findNotesFromTask(task.id()),
                "There isn't any note recorded for this task.");
    }

    private void editNote() {
        System.out.println("\n--- SELECT A TASK TO EDIT ITS NOTES ---");
        TaskDto task = listAndSelectTask();
        if (task == null) {
            return;
        }

        NoteDto noteToEdit = listAndSelectNote(task);
        if (noteToEdit == null) {
            return;
        }

        System.out.println("\nCurrent note:");
        System.out.println(NoteFormatter.format(noteToEdit));

        String newContent = NoteInputReader.readContent(noteToEdit.content());

        try {
            NoteDto updateDto = new NoteDto(
                    noteToEdit.id(),
                    noteToEdit.taskID(),
                    newContent,
                    noteToEdit.createdAt()
            );

            NoteDto updated = noteService.updateNote(updateDto);
            System.out.println("Note updated successfully.");
            System.out.println(NoteFormatter.format(updated));
        } catch (NoteContentEmpty | NoteNotFound error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void deleteNote() {
        System.out.println("\n--- SELECT A TASK TO DELETE A NOTE ---");
        TaskDto task = listAndSelectTask();
        if (task == null) {
            return;
        }

        NoteDto noteToDelete = listAndSelectNote(task);
        if (noteToDelete == null) {
            return;
        }

        System.out.println("\nSelected note:");
        System.out.println(NoteFormatter.format(noteToDelete));

        if (!ConsoleReader.confirmAction("Are you sure you want to delete this note?")) {
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

    private TaskDto listAndSelectTask() {
        List<TaskDto> tasks = taskService.listTasksByCreation();
        if (!TaskFormatter.printList(tasks, "There isn't any task recorded.")) {
            return null;
        }
        return TaskInputReader.selectTaskFromList(tasks);
    }

    private NoteDto listAndSelectNote(TaskDto task) {
        System.out.println("\nNotes for task [" + task.title() + "]:");
        List<NoteDto> notes = noteService.findNotesFromTask(task.id());
        if (!NoteFormatter.printList(notes, "There isn't any note recorded for this task.")) {
            return null;
        }
        return NoteInputReader.selectNoteFromList(notes);
    }
}
