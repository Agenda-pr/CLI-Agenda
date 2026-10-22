package common.cli;

import event.cli.EventCli;
import event.repository.EventRepository;
import event.repository.InRamEventRepository;
import event.service.EventService;
import note.cli.NoteCli;
import note.repository.InRamNoteRepository;
import note.repository.NoteRepository;
import note.service.NoteService;
import org.junit.jupiter.api.Test;
import task.cli.TaskCli;
import task.repository.InRamTaskRepository;
import task.repository.TaskRepository;
import task.service.TaskService;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

class MainMenuCliTest {

    @Test
    void showMenuPrintsLogoAndExitsOnZero() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));

        MainMenuCli mainMenuCli = buildInRamMainMenu();
        mainMenuCli.showMenu();
    }

    public static void main(String[] args) {
        MainMenuCli mainMenuCli = buildInRamMainMenu();
        mainMenuCli.showMenu();
    }

    private static MainMenuCli buildInRamMainMenu() {
        TaskRepository taskRepository = new InRamTaskRepository();
        NoteRepository noteRepository = new InRamNoteRepository();
        EventRepository eventRepository = new InRamEventRepository();

        TaskService taskService = new TaskService(taskRepository);
        NoteService noteService = new NoteService(noteRepository, taskRepository);
        EventService eventService = new EventService(eventRepository, taskRepository);

        TaskCli taskCli = new TaskCli(taskService);
        NoteCli noteCli = new NoteCli(noteService, taskService);
        EventCli eventCli = new EventCli(eventService, taskService);

        return new MainMenuCli(taskCli, noteCli, eventCli);
    }
}
